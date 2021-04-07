package ru.dennis.systems.controller.forms;

import ru.dennis.systems.config.WebConstants;
import ru.dennis.systems.config.WebContext;
import ru.dennis.systems.controller.DataDesctiption;
import ru.dennis.systems.repository.PaginationRepository;
import ru.dennis.systems.service.PaginationService;
import ru.dennis.systems.utils.PojoHeader;
import ru.dennis.systems.utils.PojoHelper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;


/**
 * @param <T>
 * @param <E>
 */
public interface ListFormSupport<POJO, T, E extends PaginationRepository<T>> extends PageCheck, PojoDependency<POJO> {


    @GetMapping(WebConstants.WEB_API_LIST)
    default String getData(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(required = false) String message,
            @RequestParam() Optional<String> orderBy,
            @RequestParam() Optional<Boolean> asc) {
        int currentPage = page.orElse(1);

        prePageCheck(null);
        model.addAttribute("message", message);
        List<PojoHeader> headers = PojoHelper.getHeaders(getPojoClass());
        PojoHelper.sortData(headers);

        getService().setPage(getDataParam(), model, currentPage, size, getQuery(),
                getContext().getConfig().paginationRequestUtils().getSorting(orderBy, asc, getPojoClass()), getEntityClass());
        model.addAttribute("headers", headers);
        model.addAttribute("dataMap",
                PojoHelper.toMap(headers,
                        getService().getPageData(currentPage,
                                getContext().getConfig().paginationRequestUtils().getSize(size, getPojoClass()),
                                getQuery(),
                                getContext().getConfig().paginationRequestUtils().getSorting(orderBy, asc, getPojoClass()), getEntityClass()),
                        getContext()));
        model.addAttribute("dataDescription", getDescription());
        return WebConstants.asPage(getPath(), WebConstants.WEB_PAGE_LIST);

    }

    Class< T> getEntityClass();


    String getPath();

    private DataDesctiption getDescription() {
        return DataDesctiption.of(getContext()
                        .getMessageTranslation(getPojoClass().getSimpleName() + ".title"),
                getId(), getPojoClass());
    }

    private String getId() {
        return getPojoClass().getSimpleName() + "_list";
    }

    default QueryObject<String> getQuery() {
        return null;
    }

    PaginationService<POJO, T, E> getService();

    default String getDataParam() {
        return "data";
    }

    WebContext.LocalWebContext getContext();

}
