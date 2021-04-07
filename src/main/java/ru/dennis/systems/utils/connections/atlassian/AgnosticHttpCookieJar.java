package ru.dennis.systems.utils.connections.atlassian;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Scope("singleton")
public class AgnosticHttpCookieJar implements CookieJar {
	Logger log = LoggerFactory.getLogger(AgnosticHttpCookieJar.class);

	private Map<String, Cookie> storage = new ConcurrentHashMap<String, Cookie>();

	@Override
	public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
		log.debug("saveFromResponse: Storing {} cookies", cookies.size());

		for (Cookie cookie : cookies) {
			log.debug("Adding cookie {}", cookie);

			storage.put(cookie.name(), cookie);
		}

		log.debug("Your cookie stor(ag)e contains {} cookies", storage.size());
	}

	@Override
	public List<Cookie> loadForRequest(HttpUrl url) {
		log.debug("loadForRequest: Loading cookies for {}", url);

		List<Cookie> cookies = new ArrayList<>();

		storage.keySet().forEach(key -> {
			Cookie cookie = storage.get(key);
			if (cookie.expiresAt() < System.currentTimeMillis()) {
				log.debug("Need to remove cookie {} ({}) because it expired at {}", key, cookie, cookie.expiresAt());
				storage.remove(key);
			} else {
				if (cookie.matches(url)) {
					log.debug("Returning cookie {} to caller", cookie);
					cookies.add(cookie);
				} else {
					log.debug("Ignoring cookie {} because it belongs to a different url", cookie);
				}
			}
		});

		log.debug("Loading {} cookies", cookies.size());

		return cookies;
	}
}