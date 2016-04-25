package org.frc3620.scout.gui.log4j2;

import java.awt.Color;
import java.io.Serializable;
import java.util.concurrent.locks.*;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;

// note: class name need not match the @Plugin name.
@SuppressWarnings("serial")
@Plugin(name="DocumentAppender", category="Core", elementType="appender", printObject=true)
public final class DocumentAppender extends AbstractAppender {
  
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    
    static public StyledDocument document = new DefaultStyledDocument();

    protected DocumentAppender(String name, Filter filter,
            Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        
        addStyle (Level.TRACE, Color.black, false, false);
        addStyle (Level.DEBUG, Color.black, false, false);
        addStyle (Level.INFO, Color.black, false, false);
        addStyle (Level.WARN, Color.RED, false, false);
        addStyle (Level.ERROR, Color.RED, true, false);
        addStyle (Level.FATAL, Color.RED, true, false);
        
        try {
        document.insertString(0, "init", document.getStyle(Level.ERROR.toString()));
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
    }

    // The append method is where the appender does the work.
    // Given a log event, you are free to do with it what you want.
    // This example demonstrates:
    // 1. Concurrency: this method may be called by multiple threads concurrently
    // 2. How to use layouts
    // 3. Error handling
    @Override
    public void append(LogEvent event) {
        readLock.lock();
        try {
          final byte[] bytes = getLayout().toByteArray(event);
          Runnable writeIt = new RunnableWriter(event.getLevel(), bytes);
          SwingUtilities.invokeLater(writeIt);

        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        } finally {
            readLock.unlock();
        }
    }
    
    private void addStyle (Level level, Color c, boolean bold, boolean italic) {
      Style style = document.addStyle(level.toString(), null);

      // Italic
      StyleConstants.setItalic(style, italic);

      // Bold
      StyleConstants.setBold(style, bold);

      // Font family
      // StyleConstants.setFontFamily(style, "SansSerif");

      // Font size
      // StyleConstants.setFontSize(style, 30);

      // Background color
      // StyleConstants.setBackground(style, Color.blue);

      // Foreground color
      StyleConstants.setForeground(style, c);

      
    }



    // Your custom appender needs to declare a factory method
    // annotated with `@PluginFactory`. Log4j will parse the configuration
    // and call this factory method to construct an appender instance with
    // the configured attributes.
    @PluginFactory
    public static DocumentAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            LOGGER.error("No name provided for DocumentAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new DocumentAppender(name, filter, layout, true);
    }
    
    class RunnableWriter implements Runnable {
      Level level;
      byte[] buf = null;
      RunnableWriter(Level level, byte[] buf) {
        this.level = level;
        this.buf = buf;
      }
      public void run() {
        try {
          Style style = document.getStyle(level.toString());
          document.insertString(document.getLength(), new String(buf), style);
        } catch (BadLocationException e) {
        }
      }
    }
}