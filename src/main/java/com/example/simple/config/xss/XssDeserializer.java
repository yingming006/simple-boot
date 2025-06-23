package com.example.simple.config.xss;

import com.example.simple.annotation.XssSafe;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * Jackson反序列化器，用于执行XSS净化。
 * 实现了 ContextualDeserializer 接口，以便在运行时根据字段注解动态选择净化策略。
 */
public class XssDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {

    /**
     * 净化模式的枚举。
     * STRIP: 默认模式，剥离所有HTML。
     * RICH_TEXT: 富文本模式，保留安全的HTML。
     */
    private enum Mode {
        STRIP,
        RICH_TEXT
    }

    private Mode mode;

    /**
     * 默认构造函数，必须存在。默认使用最严格的模式。
     */
    public XssDeserializer() {
        this.mode = Mode.STRIP;
    }

    /**
     * 带模式的构造函数。
     */
    private XssDeserializer(Mode mode) {
        this.mode = mode;
    }

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
        if (property == null) {
            return new XssDeserializer(Mode.STRIP);
        }

        XssSafe xssSafe = property.getAnnotation(XssSafe.class);

        if (Objects.nonNull(xssSafe)) {
            return new XssDeserializer(Mode.RICH_TEXT);
        }

        return new XssDeserializer(Mode.STRIP);
    }

    /**
     * 创建一个用于富文本的Jsoup安全白名单。
     * @return Safelist实例
     */
    private static Safelist createRichTextSafelist() {
        Safelist safelist = Safelist.basicWithImages();
        safelist.addTags("h1", "h2", "h3", "h4", "h5", "h6", "hr", "span", "div");
        safelist.addAttributes("a", "target");
        safelist.addAttributes(":all", "style", "class");
        safelist.addEnforcedAttribute("a", "rel", "nofollow");
        return safelist;
    }
}