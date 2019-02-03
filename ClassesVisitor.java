package parsers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import javafx.util.Pair;
import java.util.List;
import java.util.stream.Collectors;

public class ClassesVisitor extends GenericVisitorAdapter<List<Pair<String, NodeList<ClassOrInterfaceType>>>, Void> {
    @Override
    public List<Pair<String, NodeList<ClassOrInterfaceType>> >visit(CompilationUnit n, Void arg) {

        return n.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .map(p->new Pair<>(p.getNameAsString(),p.getExtendedTypes()))
                .collect(Collectors.toList());
    }
}
