package com.example.simple.config.web.xss;

import com.example.simple.annotation.XssSafe;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * Jackson反序列化器，用于执行XSS净化。
 * 实现了 ContextualDeserializer 接口，以便在运行时根据字段注解动态选择净化策略。
 */
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XssDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {

    private XssSafe.XssMode mode = XssSafe.XssMode.STRIP;

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String originalValue = p.getValueAsString();
        if (!StringUtils.hasText(originalValue)) {
            return originalValue;
        }

        switch (this.mode) {
            case RICH_TEXT:
                return Jsoup.clean(originalValue, createRichTextSafelist());
            case STRIP:
            default:
                return Jsoup.clean(originalValue, Safelist.none());
        }
    }

    /**
     * 这是实现“默认严格，注解放宽”的关键。
     * Jackson在为字段创建反序列化器时会调用此方法。
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        // 对于非String类型或没有属性上下文的场景，返回默认的反序列化器即可。
        if (property == null) {
            return this;
        }

        // 获取字段上的注解
        XssSafe xssSafe = property.getAnnotation(XssSafe.class);

        // 因为此反序列化器由注解触发，xssSafe通常不为null。
        // 如果注解中的模式与当前实例的模式不同，则创建一个新实例。
        if (Objects.nonNull(xssSafe) && !Objects.equals(xssSafe.mode(), this.mode)) {
            return new XssDeserializer(xssSafe.mode());
        }

        return this;
    }

    /**
     * 创建一个用于富文本的Jsoup安全白名单。
     * @return Safelist实例
     */
    private static Safelist createRichTextSafelist() {
        return Safelist.basicWithImages()
                .addTags("h1", "h2", "h3", "h4", "h5", "h6", "hr", "span", "div", "p", "blockquote", "ul", "ol", "li")
                .addAttributes("a", "target", "href", "title")
                .addAttributes(":all", "class")
                .addEnforcedAttribute("a", "rel", "nofollow");
    }
}