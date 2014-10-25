package com.adamdbradley.d8b.mcuproxy;

import com.adamdbradley.d8b.AppClock;
import com.adamdbradley.d8b.console.command.ScreenHelper;

class DisplayRefresher implements Runnable {

    private final Proxy proxy;

    /**
     * @param proxy
     */
    DisplayRefresher(final Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void run() {
        if (!proxy.running) {
            return;
        }

        // Just an example for now.
        // This real estate will probably be used for the
        // audio control side of the application.

        int phase = (int) ((AppClock.now().getEpochSecond() / 4) % 5);
        switch (phase) {
        case 0:
            if (this.proxy.heartbeat1IntervalCount < 5) {
                this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(0, 29, ("H1:"
                        + "WAITING    ").substring(0, 11)));
            } else {
                this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(0, 29, ("H1:"
                        + ((int) (this.proxy.heartbeat1IntervalMillisSum / this.proxy.heartbeat1IntervalCount))
                        + "           ").substring(0, 11)));
            }
            break;
        case 1:
            if (this.proxy.heartbeat2IntervalCount < 5) {
                this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(0, 29, ("H2:"
                        + "WAITING    ").substring(0, 11)));
            } else {
                this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(0, 29, ("H2:"
                        + ((int) (this.proxy.heartbeat2IntervalMillisSum / this.proxy.heartbeat2IntervalCount))
                        + "           ").substring(0, 11)));
            }
            break;
        case 2:
            if (this.proxy.pongIntervalCount < 5) {
                this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(0, 29, ("PP:"
                        + "WAIT/" + this.proxy.pongIntervalCount + "     ").substring(0, 11)));
            } else {
                this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(0, 29, ("P-:"
                        + ((int) (this.proxy.pongIntervalMillisSum / this.proxy.pongIntervalCount))
                        + "           ").substring(0, 11)));
            }
            break;
        case 3:
            this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(0, 29,
                    ("SN:" + this.proxy.getD8BSerialNumber().toString(16)
                            + "           ").substring(0, 11)));
            break;
        case 4:
            this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(0, 29,
                    "d8b-c 0.0.0"));
            break;
        }

        this.proxy.sendConsole(ScreenHelper.buildDrawScreenCommands(1, 29,
                "-----------"));
    }

}