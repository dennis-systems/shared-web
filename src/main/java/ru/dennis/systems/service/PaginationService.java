package ru.dennis.systems.service;

import ru.dennis.systems.config.WebContext;
import ru.dennis.systems.controller.forms.QueryObject;

import ru.dennis.systems.repository.PaginationRepository;
import ru.dennis.systems.repository.QueryCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class PaginationService<POJO, T, E extends PaginationRepository<T>> {

    private E repository;
    public PaginationService(E repository){

        this.repository = repository;
    }

    public void setPage(String attributeName, Model model, int currentPage, Optional<Integer> size, QueryObject<String> parameters, Sort sort, Class<T> entityClass) {
        Page<POJO> page = getPageData(currentPage, size.orElse(15), parameters, sort, entityClass);
        model.addAttribute(attributeName, page);

        int totalPages = page.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("notificationList", page.getContent());
            model.addAttribute("currentPage", currentPage);
        }

        model.addAttribute("isEmpty", page.getTotalElements() == 0);

    }

    public   Page<POJO> getPageData(int currentPage, int size, QueryObject<String> parameters, Sort sort, Class<? extends T> pojo){
        if (sort == null){
            return transform(getRepository().findAll(getContext().getRequestSpecification( pojo, getAdditionalCases(parameters) ), PageRequest.of(currentPage - 1, size)));
        }
        return transform(getRepository().findAll(getContext().getRequestSpecification( pojo, getAdditionalCases(parameters)), PageRequest.of(currentPage - 1, size, sort)));
    }

    public   List<QueryCase> getAdditionalCases(QueryObject<String> parameters){
        return Collections.emptyList();
    }

    protected Page<POJO> transform(Page<T> object) {

        List<POJO> data = new ArrayList<>();

        for (T  item : object.getContent()  ){
            data.add(transform(item));
        }

        return new PageImpl<>(data, object.getPageable() ,object.getTotalElements());

    }

    public abstract POJO transform(T item);

    public E getRepository(){
        return repository;
    }

    public abstract WebContext.LocalWebContext getContext();

}
