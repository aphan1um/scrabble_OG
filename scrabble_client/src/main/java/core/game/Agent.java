package core.game;

public class Agent {
    public enum AgentType {
        SERVER,
        PLAYER
    }

    private String name;
    private transient AgentType agentType;

    public Agent(String name, AgentType agentType) {
        this.name = name;
        this.agentType = agentType;
    }

    public Agent(AgentType agentType) {
        this.agentType = agentType;
    }

    public String getName() {
        return name;
    }

    public AgentType getAgentType() {
        return agentType;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Agent && ((Agent)obj).name.equals(name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
