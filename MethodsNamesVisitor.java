package parsers;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.List;
import java.util.stream.Collectors;

public class  MethodsNamesVisitor extends  VoidVisitorAdapter<List<String>> {
    @Override
    public void visit(ClassOrInterfaceDeclaration classe, List<String> collector) {
        collector.addAll(classe.getMethods().stream().map(p->p.getNameAsString().toString()).collect(Collectors.toList()));

    }}
