package model;

public class Puzzle {
    private final String name;
    private boolean active;
    private final boolean affectsTarget;
    private final boolean affectsPlayer;
    private final String solution;
    private final int value;
    private final String description;
    private final String effects;
    private final String target;

    public Puzzle(String name, boolean active, boolean affectsTarget,
              boolean affectsPlayer, String solution, int value,
              String description, String effects, String target) {
        this.name = name;
        this.active = active;
        this.affectsTarget = affectsTarget;
        this.affectsPlayer = affectsPlayer;
        this.solution = solution;
        this.value = value;
        this.description = description;
        this.effects = effects;
        this.target = target;
    }

    public String getName() { 
        return name; 
    }

    public boolean isActive() { 
        return active; 
    }

    public boolean affectsTarget() { 
        return affectsTarget; 
    }

    public boolean affectsPlayer() { 
        return affectsPlayer; 
    }

    public String getSolution() { 
        return solution; 
    }

    public int getValue() { 
        return value; 
    }

    public String getDescription() { 
        return description; 
    }

    public String getEffects() { 
        return effects; 
    }

    public String getTarget() { 
        return target; 
    }

    public boolean isAnswerSolution() {
        return solution != null
            && solution.startsWith("'")
            && solution.endsWith("'");
    }

    public String getCleanSolution() {
    if (isAnswerSolution()) {
        return solution.substring(1, solution.length() - 1);
    }
    return solution;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean solve(String input) {
        if (solution.startsWith("'")) {
            return input.equals(getCleanSolution());
        } 
        else {
            return input.equals(solution);
        }
    }

    public boolean solveWithItem(String itemName) {
        if (itemName == null) return false;
        if (isAnswerSolution()) return false; 
        return itemName.equalsIgnoreCase(solution);
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }
}

