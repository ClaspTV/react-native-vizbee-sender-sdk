import VizbeeEvent from "./VizbeeEvent";

/**
 * Interface for handling Vizbee events.
 * Implementations will receive events through the onEvent method.
 */
export default interface VizbeeEventHandler {
    /**
     * Called when a Vizbee event is received.
     * 
     * @param event - The event object containing the event name and data
     */
    onEvent(event: VizbeeEvent): void;
}
