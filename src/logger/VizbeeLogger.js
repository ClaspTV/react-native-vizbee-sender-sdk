// Define log levels as a constant object instead of TypeScript enum
const LogLevel = {
    Error: "error",
    Warn: "warn",
    Info: "info",
    Debug: "debug"
};

class VizbeeLogger {
    constructor() {
        this.currentLevel = LogLevel.Info;
        this.enableLogging = false;
    }

    setLevel(level) {
        this.currentLevel = level;
    }

    enable() {
        this.enableLogging = true;
    }

    disable() {
        this.enableLogging = false;
    }

    error(message, ...optionalParams) {
        if (this.canLog(LogLevel.Error)) {
            console.error(
                this.addPrefixToMessage(message),
                ...optionalParams
            );
        }
    }

    warn(message, ...optionalParams) {
        if (this.canLog(LogLevel.Warn)) {
            console.warn(
                this.addPrefixToMessage(message),
                ...optionalParams
            );
        }
    }

    info(message, ...optionalParams) {
        if (this.canLog(LogLevel.Info)) {
            console.info(
                this.addPrefixToMessage(message),
                ...optionalParams
            );
        }
    }

    debug(message, ...optionalParams) {
        if (this.canLog(LogLevel.Debug)) {
            console.debug(
                this.addPrefixToMessage(message),
                ...optionalParams
            );
        }
    }

    canLog(level) {
        if (!this.enableLogging) {
            return false;
        }

        if (this.currentLevel === LogLevel.Error) {
            return level === LogLevel.Error;
        }

        if (this.currentLevel === LogLevel.Warn) {
            return [LogLevel.Error, LogLevel.Warn].includes(level);
        }

        if (this.currentLevel === LogLevel.Info) {
            return [LogLevel.Error, LogLevel.Warn, LogLevel.Info].includes(level);
        }

        return true;
    }

    addPrefixToMessage(message) {
        return `[${new Date().toISOString()}][VZBSENDERSDK] ${message}`;
    }
}

// Export the LogLevel object to be used by consumers of the logger
exports.LogLevel = LogLevel;

// Export a singleton instance of the logger
module.exports = new VizbeeLogger();
// Add LogLevel to the exports to maintain the same API
module.exports.LogLevel = LogLevel;