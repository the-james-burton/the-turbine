package org.jimsey.projects.turbine.inlet.external;

import static java.lang.String.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.jmx.export.annotation.ManagedOperation;

import javaslang.control.Try;

public abstract class BaseDownloader {

  private static final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/55.0.2883.87 Chrome/55.0.2883.87 Safari/537.36";

  abstract Logger getLogger();

  @ManagedOperation
  public String downloadAsync(String url, String filename) {
    Runnable task = () -> Try.run(() -> downloadWithNio(url, filename))
        .getOrElseThrow(e -> new RuntimeException("unable to download", e));
    Thread thread = new Thread(task);
    thread.start();
    String msg = format("Requesting download of %s to %s", url, filename);
    getLogger().info(msg);
    return msg;
  }

  private void downloadWithNio(String url, String filename) throws Exception {
    URL website = new URL(url);
    URLConnection connection = website.openConnection();
    connection.setRequestProperty("User-Agent", userAgent);
    ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
    FileOutputStream fos = new FileOutputStream(filename);
    getLogger().info("Downloading: {} to {}", url, filename);
    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    getLogger().info("Downloaded: {} to {}", url, filename);
  }

  private void downloadWithCommons(String url, String filename) throws Exception {
    getLogger().info("Downloading: {} to {}", url, filename);
    FileUtils.copyURLToFile(new URL(url), new File(filename));
    getLogger().info("Downloaded: {} to {}", url, filename);
  }

  private void downloadOldSkool(String url, String filename) throws Exception {
    HttpURLConnection connection = null;
    BufferedOutputStream fOut = null;
    URL serverAddress = null;
    try {
      serverAddress = new URL(url);
      connection = (HttpURLConnection) serverAddress.openConnection();
      connection.setRequestMethod("GET");
      // connection.setDoOutput(true);
      connection.setReadTimeout(10000);
      connection.setRequestProperty("User-Agent", userAgent);
      getLogger().info("Downloading: {} to {}", url, filename);
      connection.connect();
      InputStream is = connection.getInputStream();
      fOut = new BufferedOutputStream(new FileOutputStream(filename));
      byte[] buffer = new byte[32 * 1024];
      int bytesRead = 0;
      while ((bytesRead = is.read(buffer)) != -1) {
        fOut.write(buffer, 0, bytesRead);
      }
      getLogger().info("Downloaded: {} to {}", url, filename);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      connection.disconnect();
      fOut.flush();
      fOut.close();
      connection = null;
    }
  }
}
