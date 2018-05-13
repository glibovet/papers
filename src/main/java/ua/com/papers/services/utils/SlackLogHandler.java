package ua.com.papers.services.utils;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import lombok.NonNull;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class SlackLogHandler extends Handler {

    private final SlackChannel crawlerChannel;
    private final SlackSession slackSession;

    public SlackLogHandler(@NonNull SlackChannel crawlerChannel, @NonNull SlackSession slackSession) {
        this.crawlerChannel = crawlerChannel;
        this.slackSession = slackSession;
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLevel() == Level.WARNING
                || record.getLevel() == Level.SEVERE) {
            slackSession.sendMessage(crawlerChannel, record.getMessage());
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
