package parsers;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class FieldsNameVisitor extends VoidVisitorAdapter<List<String>> {
@Override
public void visit(ClassOrInterfaceDeclaration classe, List<String> collector) {
        collector.addAll(classe.getFields().stream()
                .map(p->p.getVariables().get(0).getType().asString())
                .collect(Collectors.toList()));


        }}