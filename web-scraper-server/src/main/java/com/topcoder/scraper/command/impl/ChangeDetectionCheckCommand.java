package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.ChangeDetectionException;
import com.topcoder.scraper.module.ChangeDetectionCheckModule;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Change detection check Command
 */
@Component
public class ChangeDetectionCheckCommand extends AbstractCommand<ChangeDetectionCheckModule> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeDetectionCheckCommand.class);

  @Autowired
  public ChangeDetectionCheckCommand(List<ChangeDetectionCheckModule> modules) {
    super(modules);
  }

  /**
   * Run check method from module
   * @param module module to be run
   */
  @Override
  protected void process(ChangeDetectionCheckModule module) {
    try {
      module.check();
    } catch (IOException e) {
      LOGGER.error("Fail to check change detection", e);
      throw new ChangeDetectionException();
    }
    LOGGER.info("Successfully check change detection");
  }
}