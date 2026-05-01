package edu.uob.entities;

public class GameArtefact extends GameEntity {
    /**
     * An artefact object present in a location. It is by default consumable, unless set otherwise.
     *
     * @param name A name for the artefact.
     * @param description [Optional] A short note about the artefact.
     */
    public GameArtefact(String name, String description) {
        super(name, description, true);
    }

    @Override
    public String getType() {
        return "artefacts";
    }
}
