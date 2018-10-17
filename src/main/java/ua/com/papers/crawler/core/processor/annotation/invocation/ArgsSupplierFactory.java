package ua.com.papers.crawler.core.processor.annotation.invocation;

import lombok.NonNull;
import lombok.val;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.core.processor.annotation.Context;
import ua.com.papers.crawler.settings.PageSetting;

import java.util.*;
import java.util.stream.Collectors;

final class ArgsSupplierFactory {

    private final Context context;

    ArgsSupplierFactory(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    public ArgsSupplier newSupplier(@NonNull ArgumentInfo info, @NonNull Page page, @NonNull Element root, @NonNull PageSetting settings) {
        val nodes = info.getBinding().map(binding -> extractNodes(root, binding.selectors()))
                .orElseGet(() -> extractNodes(root));

        return info.getPrimaryTypeArg()
                .map(argClass -> (ArgsSupplier) new CollectionArgSupplier(info, argsForCollection(info, argClass, nodes, page, settings)))
                .orElseGet(() -> new ElementArgSupplier(info, argForElement(info, page, nodes, settings)));
    }

    private Collection<?> argsForCollection(ArgumentInfo info, Class<?> argClass, Collection<Element> nodes, Page page, PageSetting settings) {
        return (Collection) context.getCollectionTypeConverter((Class<Collection>) info.getPrimaryType(), argClass).convert(new Elements(nodes), page, settings);
    }

    private List<?> argForElement(ArgumentInfo meta, Page page, Collection<Element> nodes, PageSetting settings) {
        val c = context.getRawTypeConverter(meta.getPrimaryType());

        return nodes.stream().map(n -> c.convert(n, page, settings)).collect(Collectors.toList());
    }

    private static List<Element> extractNodes(@NonNull Element root, @NonNull String[] selectors) {
        return Arrays.stream(selectors).flatMap(css -> root.select(css).stream()).collect(Collectors.toCollection(LinkedList::new));
    }

    private static List<Element> extractNodes(@NonNull Element root) {
        val list = new ArrayList<org.jsoup.nodes.Element>(1);

        list.add(root);
        return list;
    }

}
