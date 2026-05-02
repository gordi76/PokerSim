package de.pokersim.domain;

import de.pokersim.infrastructure.RandomSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Game {
    private static final int MINIMUM_PLAYERS = 2;
    private static final int INITIAL_SMALL_BLIND = 10;

    private final GameId id;
    private final List<Player> players;
    private final List<Card> communityCards;
    private final Pot pot;
    private final ActionHistory actionHistory;
    private final BettingRules bettingRules;

    private GamePhase phase;
    private Deck deck;

    public Game(GameId id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.players = new ArrayList<>();
        this.communityCards = new ArrayList<>();
        this.pot = new Pot();
        this.actionHistory = new ActionHistory();
        this.bettingRules = new BettingRules();
        this.phase = GamePhase.WAITING_FOR_PLAYERS;
        this.deck = Deck.standard52CardDeck();
    }

    public GameId id() {
        return id;
    }

    public GamePhase phase() {
        return phase;
    }

    public List<Player> players() {
        return Collections.unmodifiableList(players);
    }

    public List<Card> communityCards() {
        return Collections.unmodifiableList(communityCards);
    }

    public Pot pot() {
        return pot;
    }

    public ActionHistory actionHistory() {
        return actionHistory;
    }

    public RoundState roundState() {
        return new RoundState(
                phase,
                BettingRound.from(phase),
                pot.total(),
                actionHistory.actions()
        );
    }

    public void addPlayer(String name, Chips chips) {
        ensureWaitingForPlayers();
        players.add(new Player(PlayerId.newId(), name, chips));
    }

    public void start(RandomSource randomSource) {
        Objects.requireNonNull(randomSource, "randomSource must not be null");
        ensureWaitingForPlayers();

        if (players.size() < MINIMUM_PLAYERS) {
            throw new IllegalStateException("at least two players are required");
        }

        deck = Deck.standard52CardDeck();
        deck.shuffle(randomSource);

        for (Player player : players) {
            player.resetForNextRound();
        }

        dealHoleCards();
        phase = GamePhase.PRE_FLOP;
        collectInitialBets();
    }

    public void advancePhase() {
        if (phase == GamePhase.WAITING_FOR_PLAYERS || phase == GamePhase.FINISHED) {
            throw new IllegalStateException("game cannot advance from phase " + phase);
        }

        switch (phase) {
            case PRE_FLOP -> revealFlop();
            case FLOP -> revealTurn();
            case TURN -> revealRiver();
            case RIVER -> {
                phase = GamePhase.SHOWDOWN;
                finish();
            }
            case SHOWDOWN -> finish();
            default -> throw new IllegalStateException("unsupported phase " + phase);
        }
    }

    public void placeBet(PlayerId playerId, Chips amount) {
        Objects.requireNonNull(playerId, "playerId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");

        if (amount.amount() <= 0) {
            throw new IllegalArgumentException("bet amount must be greater than 0");
        }

        Player player = findPlayer(playerId);
        bettingRules.validateBet(player, amount, phase);

        pot.add(player.bet(amount));
        actionHistory.record(playerId, PlayerActionType.BET, amount, phase);
    }

    public void fold(PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId must not be null");

        Player player = findPlayer(playerId);
        bettingRules.validateFold(player, phase);

        player.fold();
        actionHistory.record(playerId, PlayerActionType.FOLD, phase);
    }

    public Player determineWinner(HandEvaluator handEvaluator) {
        Objects.requireNonNull(handEvaluator, "handEvaluator must not be null");

        if (phase != GamePhase.SHOWDOWN && phase != GamePhase.FINISHED) {
            throw new IllegalStateException("winner can only be determined during showdown");
        }

        Player bestPlayer = null;
        HandRank bestRank = null;

        for (Player player : activePlayers()) {
            List<Card> cards = new ArrayList<>();
            cards.addAll(player.holeCards());
            cards.addAll(communityCards);

            HandRank rank = handEvaluator.evaluate(cards);

            if (bestPlayer == null || rank.beats(bestRank)) {
                bestPlayer = player;
                bestRank = rank;
            }
        }

        if (bestPlayer == null) {
            throw new IllegalStateException("no active player available");
        }

        return bestPlayer;
    }

    public void payOutTo(Player winner) {
        Objects.requireNonNull(winner, "winner must not be null");

        Chips payout = pot.payOut();
        winner.win(payout);
        actionHistory.record(winner.id(), PlayerActionType.WIN_POT, payout, phase);

        phase = GamePhase.FINISHED;
    }

    private void dealHoleCards() {
        for (int round = 0; round < 2; round++) {
            for (Player player : players) {
                player.receive(deck.draw());
            }
        }
    }

    private void collectInitialBets() {
        for (Player player : players) {
            if (player.chips().amount() >= INITIAL_SMALL_BLIND) {
                Chips blind = new Chips(INITIAL_SMALL_BLIND);
                pot.add(player.bet(blind));
                actionHistory.record(player.id(), PlayerActionType.SMALL_BLIND, blind, phase);
            }
        }
    }

    private void revealFlop() {
        communityCards.add(deck.draw());
        communityCards.add(deck.draw());
        communityCards.add(deck.draw());
        phase = GamePhase.FLOP;
    }

    private void revealTurn() {
        communityCards.add(deck.draw());
        phase = GamePhase.TURN;
    }

    private void revealRiver() {
        communityCards.add(deck.draw());
        phase = GamePhase.RIVER;
    }

    private void finish() {
        Player winner = determineWinner(new TexasHoldemHandEvaluator());
        payOutTo(winner);
    }

    private List<Player> activePlayers() {
        List<Player> activePlayers = new ArrayList<>();

        for (Player player : players) {
            if (!player.hasFolded()) {
                activePlayers.add(player);
            }
        }

        return activePlayers;
    }

    private Player findPlayer(PlayerId playerId) {
        for (Player player : players) {
            if (player.id().equals(playerId)) {
                return player;
            }
        }

        throw new IllegalArgumentException("unknown player id: " + playerId);
    }

    private void ensureWaitingForPlayers() {
        if (phase != GamePhase.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("game already started");
        }
    }
}