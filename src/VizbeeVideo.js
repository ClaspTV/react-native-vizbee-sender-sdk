/**
 * @class VizbeeVideo
 * @description Class representing a video in the Vizbee system
 */
export default class VizbeeVideo {
    /**
     * @constructor
     * @description Initializes a new instance of VizbeeVideo
     */
    constructor() {
        /**
         * @property {string|null} guid - Globally Unique Identifier for the video
         */
        this.guid = null;

        // metadata
        /**
         * @property {string} title - The title of the video
         */
        this.title = "";

        /**
         * @property {string} subtitle - The subtitle of the video
         */
        this.subtitle = "";

        /**
         * @property {string} imageUrl - URL of the video thumbnail or poster
         */
        this.imageUrl = "";

        /**
         * @property {boolean} isLive - Indicates if the video is a live stream
         */
        this.isLive = false;

        // streamInfo
        /**
         * @property {string|null} streamUrl - URL of the video stream
         */
        this.streamUrl = null;

        /**
         * @property {Object} tracks - Object containing track information
         */
        this.tracks = {};

        /**
         * @property {number} startPositionInSeconds - Start position of the video in seconds
         */
        this.startPositionInSeconds = 0;

        // custom 
        /**
         * @property {Object} customProperties - Custom properties associated with the video
         */
        this.customProperties = {};
    }
}