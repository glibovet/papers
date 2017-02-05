package ua.com.papers.crawler;

import com.google.common.base.Preconditions;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.util.PageHandler;
import ua.com.papers.crawler.util.PostHandle;
import ua.com.papers.crawler.util.PreHandle;
import ua.com.papers.crawler.util.Url;

import javax.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Максим on 11/27/2016.
 */
@PageHandler(id = 3)
public class HandlerDemo {

    private final String filePath;
    private BufferedWriter writer;

    public HandlerDemo(@NotNull String filePath) {
        this.filePath = Preconditions.checkNotNull(filePath);
    }

    @PreHandle
    public void onPrepare() {
        // prepare instance
        try {
            // re-write file
            writer = new BufferedWriter(new FileWriter(filePath, true));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        writeLine("----------------------------");
    }

    @PostHandle
    public void onFinish(Page page) {
        // analyzing is done
        System.out.println("onFinish#");

        try {
            writer.flush();
            writer.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    //@Handler(id = 1, converter = StringAdapter.class)
    public void onHandlePart1(String str) {
        writeLine(str);
    }

   // @Handler(id = 2, converter = StringAdapter.class)
    public void onHandlePart2(String str) {
        writeLine(str);
    }

  //  @Handler(id = 3, converter = ImageUrlAdapter.class)
    public void onHandleImage(Url url) {
        writeLine(url == null ? "Failed to parse url" : url.toString());
    }

    private void writeLine(String str) {

        try {
            writer.write(str);
            writer.newLine();
            writer.flush();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
