// Define log levels as an enum for better type safety
enum LogLevel {
    Error = "error",
    Warn = "warn",
    Info = "info",
    Debug = "debug"
}

class VizbeeLogger {
    private currentLevel: LogLevel;
    private enableLogging: boolean;

    constructor() {
        this.currentLevel = LogLevel.Info;
        this.enableLogging = false;
    }

    public setLevel(level: LogLevel): void {
        this.currentLevel = level;
    }

    public enable(): void {
        this.enableLogging = true;
    }

    public disable(): void {
        this.enableLogging = false;
    }

    public error(message: string, ...optionalParams: any[]): void {
        if (this.canLog(LogLevel.Error)) {
            console.error(
                this.addPrefixToMessage(message),
                ...optionalParams
            );
        }
    }

    public warn(message: string, ...optionalParams: any[]): void {
        if (this.canLog(LogLevel.Warn)) {
            console.warn(
                this.addPrefixToMessage(message),
                ...optionalParams
            );
        }
    }

    public info(message: string, ...optionalParams: any[]): void {
        if (this.canLog(LogLevel.Info)) {
            console.info(
                this.addPrefixToMessage(message),
                ...optionalParams
            );
        }
    }

    public debug(message: string, ...optionalParams: any[]): void {
        if (this.canLog(LogLevel.Debug)) {
            console.debug(
                this.addPrefixToMessage(message),
                ...optionalParams
            );
        }
    }

    private canLog(level: LogLevel): boolean {
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

    private addPrefixToMessage(message: string): string {
        return `[${new Date().toISOString()}][VIZBEEHOMESSO] ${message}`;
    }
}

// Export the LogLevel enum to be used by consumers of the logger
export { LogLevel };

// Export a singleton instance of the logger
export default new VizbeeLogger();