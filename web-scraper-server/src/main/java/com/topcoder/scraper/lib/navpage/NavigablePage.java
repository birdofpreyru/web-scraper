package com.topcoder.scraper.lib.navpage;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.service.WebpageService;

import groovy.lang.Closure;

public class NavigablePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigablePage.class);

    protected TrafficWebClient webClient;
    protected HtmlPage page;

    public NavigablePage(HtmlPage page, TrafficWebClient webClient) {
        LOGGER.debug("[constructor] in");
        this.webClient = webClient;
        this.page = page;
    }

    public NavigablePage(String url, TrafficWebClient webClient) {
        LOGGER.debug("[constructor] in");
        this.webClient = webClient;
        setPage(url); // TODO: Will this work if page is uninitialized?
    }

    public void setEnableJS(boolean value) {
        LOGGER.debug("[setEnableJS] in");
        LOGGER.debug("[setEnableJS] Setting JavaScript Enabled to " + value);
        webClient.getWebClient().getOptions().setJavaScriptEnabled(value);
    }
    /* Get and Set */

    public void setPage(String url) {
        LOGGER.debug("[setPage] in");
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {

            e.printStackTrace();
            LOGGER.info("Could not set page to " + url + " in NavigablePage.java");
        }
    }

    public void setPage(HtmlPage page) {
        LOGGER.debug("[setPage] in");
        this.page = page;
    }

    public HtmlPage getPage() {
        LOGGER.debug("[getPage] in");
        return page;
    }

    /* Navigate */

    public void click(String selector) {
        LOGGER.debug("[click] in");
        if (selector != null) {
            HtmlElement element = page.querySelector(selector);
            LOGGER.info("click() > Selected " + element + " from " + selector);
            if (element == null) {
              return;
            }
            try {
                HtmlPage result = webClient.click(element);
                if (result != null) {
                    page = result;
                }
                LOGGER.info("Setting page to " + result);
            } catch (Exception e) {
                LOGGER.error(String.format("Failed to perform click on the element selected by '%s'. page: %s", selector, page.getUrl()), e);
            }
        }
    }

    public void click(DomNode node, String selector) {
        LOGGER.debug("[click] in");

        if (node != null && selector != null) {
            DomElement element = node.querySelector(selector);
            LOGGER.info("click() > Selected " + element + " from " + selector);
            if (element != null) {
                try {
                    HtmlPage result = webClient.click(element);
                    LOGGER.info("CLICKED ELEMENT>>> " + result);
                    if (result != null)
                        page = result;
                    // savePage("pageClicked", "yahoo", webPageServiceWired);
                } catch (IOException e) {
                    LOGGER.info("Could not navigate to " + selector + " in NavigablePage.java");
                    e.printStackTrace();
                }
            }
        }
    }

    public void click(String selector, WebpageService webpageService) {
        LOGGER.debug("[click] in");

        if (selector != null && webpageService != null) {
            HtmlElement element = page.querySelector(selector);
            LOGGER.info("click() > Selected " + element + " from " + selector);
            if (element != null) {
                try {
                    HtmlPage result = webClient.click(element);
                    if (result != null)
                        page = result;
                    // savePage("pageClicked", "yahoo", webpageService);
                    webpageService.save("pageClicked", "yahoo", page.getWebResponse().getContentAsString());
                } catch (IOException e) {
                    LOGGER.info("Could not navigate to " + selector + " in NavigablePage.java");
                    e.printStackTrace();
                }
            }
        }
    }

    public void openPage(DomNode node, String selector, Closure<Boolean> closure, WebpageService webpageService) {
        LOGGER.debug("[openPage] in");

        if (node != null && selector != null) {
            HtmlAnchor anchorNode = node.querySelector(selector);
            LOGGER.debug("[openPage] open > Selected " + anchorNode + " from " + selector);
            if (anchorNode != null) {
                try {
                    HtmlPage subPage = webClient.getPage(anchorNode.getHrefAttribute());
                    webpageService.save("sub-page-opened", "yahoo", subPage.getWebResponse().getContentAsString());
                    LOGGER.debug("[openPage] CLICKED ELEMENT>>> " + subPage);
                    if (subPage != null) {
                        closure.call(subPage);
                    }
                } catch (IOException e) {
                    LOGGER.debug("[openPage] Could not navigate to " + selector + " in NavigablePage.java");
                    e.printStackTrace();
                }
            }
        }
    }

    public String getText(String selector) {
        LOGGER.debug("[getText] in");

        HtmlElement node = page.querySelector(selector);
        String str = node != null ? node.asText().replaceAll("\\n", " ").trim() : null;
        LOGGER.info(">>> Got Text >>> " + str + " for " + selector);
        return str;
    }

    public String getText(String... selectors) {
      if (selectors == null || selectors.length == 0) {
        return null;
      }
      for (String s : selectors) {
        String text = getText(s);
        if (text != null) {
          return text;
        }
      }
      return null;
    }

    public String getNodeAttribute(String selector, String attr) {
      LOGGER.debug("[getNodeAttribute(" + attr + ")] in");

      HtmlElement node = page.querySelector(selector);
      String str = node != null ? node.getAttribute(attr) : null;
      LOGGER.info(">>> Got Attribute >>> " + str + " for " + selector);
      return str;
    }

    protected String getValue(String selector) {
        LOGGER.debug("[getValue] in");

        // HtmlElement node = page.querySelector(selector);
        // String str = node != null ? node.get
        // LOGGER.info(">>> Got Text >>> " + str + " for " + selector);
        LOGGER.debug("NavigablePage.getValue(Str) -- This method is WIP");
        // get the value of the selected thing, not the text value, when JSON object is
        // returned
        String str = null;
        return str;
    }


    public String getText(DomNode sourceNode, String selector) {
        LOGGER.debug("[getText] in");

        HtmlElement node = sourceNode.querySelector(selector);
        String str = node != null ? node.asText().replaceAll("\\n", " ").trim() : null;
        LOGGER.info(">>> Got Text >>> " + str + " for " + selector);
        return str;
    }

    public String getNodeAttribute(DomNode sourceNode, String selector, String attr) {
      LOGGER.debug("[getNodeAttribute(" + attr + ")] in");

      HtmlElement node = sourceNode.querySelector(selector);
      String str = node != null ? node.getAttribute(attr) : null;
      LOGGER.info(">>> Got Attribute >>> " + str + " for " + selector);
      return str;
    }

    public void type(String input, String selector) {
        LOGGER.debug("[type] in");

        if (input != null && selector != null) {
            HtmlTextInput memberIdInput = page.querySelector(selector);
            if (memberIdInput != null) {
                LOGGER.info("type() > Selected " + memberIdInput + " from " + selector);
                // LOGGER.info("Typing " + input); //TODO: REMOVE
                try {
                    memberIdInput.type(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void typePassword(String input, String selector) {
        LOGGER.debug("[typePassword] in");

        if (input != null && selector != null) {
            HtmlPasswordInput memberIdInput = page.querySelector(selector);
            LOGGER.info("typePassword() > Selected " + memberIdInput + " from " + selector);
            if (memberIdInput != null) {
                try {
                    memberIdInput.type(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void typeCheckbox(String input, String selector) {
        LOGGER.debug("[typeCheckbox] in");

        if (input != null && selector != null) {
            // value should be "on" or "off"
            HtmlCheckBoxInput memberIdInput = page.querySelector(selector);
            if (memberIdInput != null) {
                LOGGER.info("typeCheckBox() > Selected " + memberIdInput + " from " + selector);
                try {
                    memberIdInput.type(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String savePage(String fileName, String siteName, WebpageService webpageService) {
        LOGGER.debug("[savePage] in");
        if (page == null || page.getWebResponse() == null) {
          return null;
        }
        return webpageService.save(fileName, siteName, page.getWebResponse().getContentAsString());
    }

    public URL getPageUrl() {
      return this.page != null ? this.page.getUrl() : null;
    }
}
