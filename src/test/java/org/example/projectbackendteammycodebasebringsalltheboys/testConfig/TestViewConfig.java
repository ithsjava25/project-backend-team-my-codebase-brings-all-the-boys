package org.example.projectbackendteammycodebasebringsalltheboys.testConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import java.util.Map;

@TestConfiguration
public class TestViewConfig {
    @Bean
    @Primary
    public ViewResolver viewResolver() {
        return (viewName, locale) -> {
            if (viewName.startsWith("redirect:") || viewName.startsWith("forward:")) {
                return null;
            }
            AbstractUrlBasedView view = new AbstractUrlBasedView() {
                @Override
                protected void renderMergedOutputModel(Map<String, Object> model,
                                                       HttpServletRequest request, HttpServletResponse response) {
                    // no-op
                }
            };
            view.setBeanName(viewName);
            return view;
        };
    }
}
