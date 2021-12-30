package org.apache.sling.sitemap.builder.extensions;

import org.apache.sling.sitemap.builder.Extension;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * An extension to add image links and metadata to an {@link org.apache.sling.sitemap.builder.Url}.
 *
 * @see <a href="https://developers.google.com/search/docs/advanced/sitemaps/image-sitemaps">Image sitemaps</a>
 */
@ProviderType
public interface GoogleImageExtension extends Extension {

    @NotNull
    GoogleImageExtension setUrl(@NotNull String location);

    @NotNull
    GoogleImageExtension setCaption(String caption);

    @NotNull
    GoogleImageExtension setGeoLocation(String geoLocation);

    @NotNull
    GoogleImageExtension setTitle(String title);

    @NotNull
    GoogleImageExtension setLicense(String licenseLocation);

}
