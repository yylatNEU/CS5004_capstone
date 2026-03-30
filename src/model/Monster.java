package model;

public class Monster {
    private final String name;
    private boolean active;
    private final boolean affectsTarget;
    private final boolean affectsPlayer;
    private final String solution;
    private final int value;
    private final String description;
    private final String effects;
    private final int damage;
    private final String target;
    private final boolean canAttack;
    private final String attack;
    
    public Monster(String name, boolean active, boolean affectsTarget,
                   boolean affectsPlayer, String solution, int value,
                   String description, String effects, int damage,
                   String target, boolean canAttack, String attack) {
        this.name = name;
        this.active = active;
        this.affectsTarget = affectsTarget;
        this.affectsPlayer = affectsPlayer;
        this.solution = solution;
        this.value = value;
        this.description = description;
        this.effects = effects;
        this.damage = damage;
        this.target = target;
        this.canAttack = canAttack;
        this.attack = attack;
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
    public int getDamage() { 
        return damage; 
    }
    public String getTarget() {
        return target; 
    }
    public boolean canAttack() { 
        return canAttack; 
    }
    public String getAttack() { 
        return attack; 
    }

    public void deactivate() { 
        this.active = false; 
    }

    @Override
    public String toString() { 
        return name.toUpperCase(); 
    }
}
