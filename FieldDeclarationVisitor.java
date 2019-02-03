package parsers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;

public class FieldDeclarationVisitor extends VoidVisitorAdapter<HashMap<String,Integer>> {
    @Override
    public void visit(CompilationUnit AST, HashMap<String, Integer> collector) {
        super.visit(AST, collector);
        AST.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .forEach(e->collector.put(e.getName().asString(),e.getFields().size()));

    }}

