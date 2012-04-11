package fr.rmannibucau.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;

/**
 * @author Romain Manni-Bucau
 */
public class ExampleRouteBuilder extends RouteBuilder {
    @Override public void configure() throws Exception {
        from("vm:a")
            .routeId("java-dsl-route")
            .split(body().tokenize(" "))
                .to("mock:splitep")
            .end()
            .aggregate(header("cheese"), new GroupedExchangeAggregationStrategy())
                .completionSize(10)
                .to("direct:aggreg")
            .end()
            .to("seda:b");
    }
}
