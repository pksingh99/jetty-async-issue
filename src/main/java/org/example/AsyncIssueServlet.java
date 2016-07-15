package org.example;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(asyncSupported = true)
public class AsyncIssueServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AsyncContext context = request.startAsync();
        ReadWriteListener listener = new ReadWriteListener(context);
    }

    private static class ReadWriteListener implements ReadListener, WriteListener {

        private final AsyncContext context;

        private final StringBuilder builder = new StringBuilder();

        private final ServletInputStream in;

        private final ServletOutputStream out;

        ReadWriteListener(AsyncContext context) throws IOException {
            this.context = context;
            this.in = context.getRequest().getInputStream();
            this.out = context.getResponse().getOutputStream();
            this.in.setReadListener(this);
            this.out.setWriteListener(this);
        }

        @Override
        public void onDataAvailable() throws IOException {
            System.out.println("onDataAvailable");
            byte[] buffer = new byte[1024];
            int read = 0;
            while (this.in.isReady()) {
                read = this.in.read(buffer);
                if (read == -1) {
                    break;
                }
                if (this.out.isReady()) {
                    this.out.write(buffer, 0, read);
                } else {
                    this.builder.append(new String(buffer, 0, read));
                }
            }
        }

        @Override
        public void onAllDataRead() throws IOException {
            System.out.println("onAllDataRead");
            System.out.println(this.builder.toString());
            this.context.complete();
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError: " + throwable);
            throwable.printStackTrace();
        }

        @Override
        public void onWritePossible() throws IOException {
            System.out.println("onWritePossible");
            while (this.out.isReady()) {
                this.out.flush();
            }
        }

    }
}
