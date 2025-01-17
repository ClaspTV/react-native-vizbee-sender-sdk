import logger from '../logger/VizbeeLogger';
import { VizbeeEventHandler } from './VizbeeEventHandler';
import { VizbeeEvent } from './VizbeeEvent';
import { NativeModules } from 'react-native';
import VizbeeManager from '../VizbeeManager';

const VizbeeNativeManager = NativeModules.VizbeeNativeManager || {};

/**
 * Manages event handling between the React Native layer and native platforms.
 * Provides functionality to register/unregister event handlers, send events,
 * and handle incoming events from the native layer.
 */
export class VizbeeEventManager {
    private readonly logTag = 'EventManager';
    private eventHandlers: Map<string, Set<VizbeeEventHandler>>;
    private eventSubscription: number | undefined;

    //------------------
    // Lifecycle
    //------------------

    constructor() {
        this.eventHandlers = new Map();
        this.initializeEventHandling();
    }

    /**
     * Deinitializes the event manager by cleaning up event subscriptions
     * and clearing all registered handlers.
     */
    deinitialize(): void {
        logger.debug(
            this.logTag,
            'Deinitializing VizbeeEventManager'
        );

        this.cleanupEventHandling();
        this.eventHandlers.clear();
    }

    //------------------
    // Private Methods
    //------------------

    /**
     * Initializes event handling by subscribing to the VZB_EVENT from VizbeeManager.
     * Creates a subscription that listens for events and processes them through onEvent.
     * @private
     */
    private initializeEventHandling(): void {
        this.eventSubscription = VizbeeManager.addListener('VZB_EVENT', 
            (eventData: { name: string; data: Record<string, any> }) => {
                logger.debug(
                    this.logTag,
                    `Received VZB_EVENT: ${JSON.stringify(eventData)}`
                );
                const vizbeeEvent = new VizbeeEvent(eventData.name, eventData.data);
                this.onEvent(vizbeeEvent);
            }
        );
    }

    /**
     * Cleans up event handling by removing the event subscription.
     * Should be called before the manager is destroyed.
     * @private
     */
    private cleanupEventHandling(): void {
        if (this.eventSubscription !== undefined) {
            VizbeeManager.removeListener(this.eventSubscription);
            this.eventSubscription = undefined;
        }
    }

    //------------------
    // Event Registration
    //------------------

    /**
     * Registers an event handler for a specific event name.
     * If the handler is already registered for this event, the registration is skipped.
     * Also notifies the native layer about this registration.
     * 
     * @param eventName - The name of the event to register for
     * @param eventHandler - The handler to be called when the event occurs
     */
    registerForEvent(eventName: string, eventHandler: VizbeeEventHandler): void {
        let handlers = this.eventHandlers.get(eventName);
        if (!handlers) {
            handlers = new Set();
            this.eventHandlers.set(eventName, handlers);
        }

        if (handlers.has(eventHandler)) {
            logger.debug(
                this.logTag,
                `Event handler already registered for event = ${eventName}, skipping registration`
            );
            return;
        }

        logger.debug(
            this.logTag,
            `Register event handler = ${eventHandler} for event = ${eventName}`
        );
        
        handlers.add(eventHandler);
        VizbeeNativeManager.registerForEvent(eventName);
    }

    /**
     * Unregisters an event handler for a specific event name.
     * If the handler is not found, logs an error but does not throw.
     * Also notifies the native layer about this unregistration.
     * 
     * @param eventName - The name of the event to unregister from
     * @param eventHandler - The handler to remove
     */
    unregisterForEvent(eventName: string, eventHandler: VizbeeEventHandler): void {
        logger.debug(
            this.logTag,
            `Unregister event handler = ${eventHandler} for event = ${eventName}`
        );

        const handlers = this.eventHandlers.get(eventName);
        if (handlers) {
            handlers.delete(eventHandler);
        } else {
            logger.error(
                this.logTag,
                `Unregister event handler = ${eventHandler} for event = ${eventName} not found`
            );
        }

        VizbeeNativeManager.unregisterForEvent(eventName);
    }

    //------------------
    // Event Handling
    //------------------

    /**
     * Sends an event to the native layer.
     * 
     * @param event - The event to send to the native layer
     */
    sendEvent(event: VizbeeEvent): void {
        logger.debug(
            this.logTag,
            `Send event = ${event.name} with data = ${JSON.stringify(event.data)}`
        );

        // Send event to native module
        VizbeeNativeManager.sendEvent(event.name, event.data);
    }

    /**
     * Handles incoming events by distributing them to registered handlers.
     * Logs the event processing and handler invocation for debugging.
     * 
     * @param event - The event to process and distribute to handlers
     */
    onEvent(event: VizbeeEvent): void {
        logger.debug(
            this.logTag,
            `onEvent invoked with event = ${JSON.stringify(event)}`
        );

        const handlers = this.eventHandlers.get(event.name);
        handlers?.forEach(handler => {
            logger.debug(
                this.logTag,
                `onEvent - propagating onEvent to handler = ${handler}`
            );
            handler.onEvent(event);
        });
    }
}