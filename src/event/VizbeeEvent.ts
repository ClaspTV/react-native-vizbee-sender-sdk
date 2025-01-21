/**
 * Represents an event in the Vizbee system.
 * Contains an event name and associated data.
 * This class ensures immutability of its properties.
 */
export default class VizbeeEvent {
    /**
     * Creates a new VizbeeEvent instance.
     * 
     * @param _name - The name of the event
     * @param _data - The data associated with the event
     */
    constructor(
        private readonly _name: string,
        private readonly _data: Record<string, any>
    ) {}

    /**
     * Gets the name of the event.
     * 
     * @returns The event name
     */
    get name(): string {
        return this._name;
    }

    /**
     * Gets the data associated with the event.
     * Returns a copy of the data to maintain immutability.
     * 
     * @returns A copy of the event data
     */
    get data(): Record<string, any> {
        return { ...this._data };
    }
}