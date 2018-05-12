package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import ua.com.papers.crawler.core.processor.convert.Converter;
import ua.com.papers.crawler.settings.v2.process.Binding;

import javax.annotation.Nullable;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class ArgumentInfo {

    boolean isNullable;
    Converter<?> converter;
    @Nullable Binding binding;

    ArgumentInfo(boolean isNullable, @NonNull Converter<?> converter, @NonNull Binding binding) {
        this.isNullable = isNullable;
        this.binding = binding;
        this.converter = converter;
    }

    ArgumentInfo(boolean isNullable, @NonNull Converter<?> converter) {
        this.isNullable = isNullable;
        this.binding = null;
        this.converter = converter;
    }

}
