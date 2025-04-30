package init.project.global.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

public class YamlPropertyConfig implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        Resource resource = encodedResource.getResource();
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource);
        factory.afterPropertiesSet();

        Properties properties = factory.getObject();
        if (properties == null) {
            throw new IllegalStateException("YAML 파일을 로드할 수 없습니다: " + resource.getFilename());
        }
        String sourceName = (resource.getFilename() != null)
                ? resource.getFilename()
                : (name != null ? name : "application.yml");
        return new PropertiesPropertySource(sourceName, properties);
    }

}