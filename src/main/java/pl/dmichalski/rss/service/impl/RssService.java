package pl.dmichalski.rss.service.impl;

import org.springframework.stereotype.Service;
import pl.dmichalski.rss.entity.RssFeedEntryEntity;
import pl.dmichalski.rss.exception.RSSException;
import pl.dmichalski.rss.rss.ObjectFactory;
import pl.dmichalski.rss.rss.TRss;
import pl.dmichalski.rss.rss.TRssChannel;
import pl.dmichalski.rss.rss.TRssItem;
import pl.dmichalski.rss.service.IRssService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Author: Daniel
 */
@Service
public class RssService implements IRssService{

    private List<RssFeedEntryEntity> getItems(Source source) throws RSSException {
        List<RssFeedEntryEntity> list = new ArrayList<>();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<TRss> jaxbElement = unmarshaller.unmarshal(source, TRss.class);
            TRss rss = jaxbElement.getValue();

            List<TRssChannel> channel = rss.getChannel();
            for (TRssChannel chanel : channel) {
                List<TRssItem> items = chanel.getItem();
                for (TRssItem rssItem : items) {
                    RssFeedEntryEntity rssFeedEntryEntity = new RssFeedEntryEntity();
                    rssFeedEntryEntity.setTitle(rssItem.getTitle());
                    rssFeedEntryEntity.setDescription(rssItem.getDescription());
                    rssFeedEntryEntity.setLink(rssItem.getLink());
                    Date pubDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                            .parse(rssItem.getPubDate());
                    rssFeedEntryEntity.setPublishedDate(pubDate);
                    list.add(rssFeedEntryEntity);
                }
            }

        } catch (JAXBException | ParseException e) {
            throw new RSSException(e);
        }

        return list;
    }

    public List<RssFeedEntryEntity> getItems(File file) throws RSSException {
        return getItems(new StreamSource(file));
    }

    public List<RssFeedEntryEntity> getItems(String url) throws RSSException {
        return getItems(new StreamSource(url));
    }

}
