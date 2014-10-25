package com.adamdbradley.d8b.mcuproxy;

import com.adamdbradley.d8b.console.command.Ping;

class ConsolePinger implements Runnable {

    private final Proxy proxy;

    /**
     * @param proxy
     */
    ConsolePinger(final Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void run() {
        if (!proxy.running) {
            return;
        }

        proxy.sendConsole(new Ping());
    }

}
