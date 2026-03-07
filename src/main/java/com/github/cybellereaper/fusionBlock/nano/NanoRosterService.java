package com.github.cybellereaper.fusionBlock.nano;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Maintains a player's Nano Book, equipped slots, summon lifecycle and power usage.
 */
public class NanoRosterService {
    private static final int MAX_EQUIPPED_SLOTS = 3;
    private static final double BASE_SUMMON_DRAIN_PER_SECOND = 1.0;
    private static final Duration GUMBALL_DURATION = Duration.ofMinutes(10);
    private static final double GUMBALL_POWER_BOOST = 0.20;

    private final Clock clock;
    private final List<String> equippedNanoIds = new ArrayList<>();
    private final Map<String, NanoState> nanoBook = new HashMap<>();
    private String summonedNanoId;

    public NanoRosterService() {
        this(Clock.systemUTC());
    }

    public NanoRosterService(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public void acquireNano(Nano nano, List<NanoPower> powerChoices, int selectedPowerIndex) {
        Objects.requireNonNull(nano, "nano");
        Objects.requireNonNull(powerChoices, "powerChoices");
        if (powerChoices.size() != 3) {
            throw new IllegalArgumentException("A Nano must present exactly three powers on acquisition.");
        }
        if (powerChoices.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Power choices cannot contain null values.");
        }
        if (selectedPowerIndex < 0 || selectedPowerIndex >= powerChoices.size()) {
            throw new IllegalArgumentException("Selected power index is out of range.");
        }
        if (nanoBook.containsKey(nano.getId())) {
            throw new IllegalArgumentException("Nano already exists in the Nano Book.");
        }

        var selectedPower = powerChoices.get(selectedPowerIndex);
        var availablePowers = List.copyOf(powerChoices);
        var uniquePowerIds = availablePowers.stream().map(NanoPower::getId).distinct().count();
        if (uniquePowerIds != availablePowers.size()) {
            throw new IllegalArgumentException("Power ids must be unique per Nano.");
        }
        var powersById = availablePowers.stream()
                .collect(Collectors.toUnmodifiableMap(NanoPower::getId, power -> power));
        nanoBook.put(nano.getId(), new NanoState(nano, availablePowers, powersById, selectedPower));
    }

    public Map<String, Nano> getNanoBook() {
        return nanoBook.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> entry.getValue().nano()));
    }

    public List<String> getEquippedNanoIds() {
        return List.copyOf(equippedNanoIds);
    }

    public String getSummonedNanoId() {
        return summonedNanoId;
    }

    public void equipNano(String nanoId) {
        requireNanoExists(nanoId);
        if (equippedNanoIds.contains(nanoId)) {
            return;
        }
        if (equippedNanoIds.size() >= MAX_EQUIPPED_SLOTS) {
            throw new IllegalStateException("Only three Nanos can be equipped.");
        }
        equippedNanoIds.add(nanoId);
    }

    public void unequipNano(String nanoId) {
        requireNanoExists(nanoId);
        equippedNanoIds.remove(nanoId);
        if (Objects.equals(summonedNanoId, nanoId)) {
            unsummon();
        }
    }

    public void summon(String nanoId) {
        if (!equippedNanoIds.contains(nanoId)) {
            throw new IllegalStateException("Nano must be equipped before summoning.");
        }

        NanoState state = requireNanoExists(nanoId);
        if (state.nano().getStamina() <= 0.0) {
            throw new IllegalStateException("Nano has no stamina.");
        }

        unsummon();
        state.nano().setSummoned(true);
        summonedNanoId = nanoId;
    }

    public void unsummon() {
        if (summonedNanoId == null) {
            return;
        }

        NanoState summoned = nanoBook.get(summonedNanoId);
        if (summoned != null) {
            summoned.nano().setSummoned(false);
        }
        summonedNanoId = null;
    }

    public void updateSummon(double elapsedSeconds) {
        if (summonedNanoId == null || elapsedSeconds <= 0.0) {
            return;
        }

        NanoState summonedState = requireNanoExists(summonedNanoId);
        Nano summonedNano = summonedState.nano();
        double staminaDrain = BASE_SUMMON_DRAIN_PER_SECOND * elapsedSeconds;
        NanoPower activePower = summonedState.selectedPower();
        if (activePower.getMode() == NanoPowerMode.PASSIVE) {
            staminaDrain += activePower.getStaminaDrainPerSecond() * elapsedSeconds;
        }

        summonedNano.damageStamina(staminaDrain);
        if (!summonedNano.isSummoned()) {
            summonedNanoId = null;
        }
    }

    public void triggerPower() {
        if (summonedNanoId == null) {
            throw new IllegalStateException("No Nano is currently summoned.");
        }

        NanoState summonedState = requireNanoExists(summonedNanoId);
        NanoPower selectedPower = summonedState.selectedPower();
        if (selectedPower.getMode() != NanoPowerMode.TRIGGERED) {
            throw new IllegalStateException("Selected power is passive and cannot be manually triggered.");
        }

        summonedState.nano().damageStamina(selectedPower.getStaminaCostOnTrigger());
        if (!summonedState.nano().isSummoned()) {
            summonedNanoId = null;
        }
    }

    public NanoEffectType.AdvantageIndicator getCombatIndicatorAgainst(NanoEffectType attackerType,
                                                                        NanoEffectType defenderType) {
        return attackerType.getAdvantageIndicatorAgainst(defenderType);
    }

    public double getCombatDamageMultiplier(NanoEffectType attackerType, NanoEffectType defenderType) {
        return attackerType.getDamageMultiplierAgainst(defenderType);
    }

    public void swapPowerAtNanoStation(String nanoId, String powerId, boolean atNanoStation) {
        if (!atNanoStation) {
            throw new IllegalStateException("Power swapping requires a Nano Station.");
        }
        Objects.requireNonNull(powerId, "powerId");

        var state = requireNanoExists(nanoId);
        var selectedPower = state.powersById().get(powerId);
        if (selectedPower == null) {
            throw new IllegalArgumentException("Power does not belong to this Nano.");
        }
        state.selectPower(selectedPower);
    }

    public void applyGumball(String nanoId, NanoEffectType gumballType) {
        NanoState state = requireNanoExists(nanoId);
        if (state.nano().getType() != gumballType) {
            throw new IllegalArgumentException("Gumball type must match Nano type.");
        }

        Instant expiry = clock.instant().plus(GUMBALL_DURATION);
        state.nano().setPowerBoosted(GUMBALL_POWER_BOOST);
        state.setGumballExpiry(expiry);
    }

    public void refreshBoosts() {
        Instant now = clock.instant();
        for (NanoState state : nanoBook.values()) {
            if (state.gumballExpiry() != null && !now.isBefore(state.gumballExpiry())) {
                state.nano().setPowerBoosted(0.0);
                state.setGumballExpiry(null);
            }
        }
    }

    public NanoPower getSelectedPower(String nanoId) {
        return requireNanoExists(nanoId).selectedPower();
    }

    private NanoState requireNanoExists(String nanoId) {
        Objects.requireNonNull(nanoId, "nanoId");
        var state = nanoBook.get(nanoId);
        if (state == null) {
            throw new IllegalArgumentException("Unknown Nano id: " + nanoId);
        }
        return state;
    }

    private static final class NanoState {
        private final Nano nano;
        private final List<NanoPower> powerChoices;
        private final Map<String, NanoPower> powersById;
        private NanoPower selectedPower;
        private Instant gumballExpiry;

        private NanoState(Nano nano,
                          List<NanoPower> powerChoices,
                          Map<String, NanoPower> powersById,
                          NanoPower selectedPower) {
            this.nano = nano;
            this.powerChoices = powerChoices;
            this.powersById = powersById;
            this.selectedPower = selectedPower;
        }

        private Nano nano() {
            return nano;
        }

        private List<NanoPower> powerChoices() {
            return powerChoices;
        }

        private Map<String, NanoPower> powersById() {
            return powersById;
        }

        private NanoPower selectedPower() {
            return selectedPower;
        }

        private void selectPower(NanoPower power) {
            this.selectedPower = power;
        }

        private Instant gumballExpiry() {
            return gumballExpiry;
        }

        private void setGumballExpiry(Instant gumballExpiry) {
            this.gumballExpiry = gumballExpiry;
        }
    }
}
