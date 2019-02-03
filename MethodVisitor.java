package parsers;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodVisitor extends GenericVisitorAdapter<Integer,Void> {

    private long count;
    @Override
    public Integer visit(ClassOrInterfaceDeclaration n, Void ag)
    {
        Integer number = n.getMethods().size();
        return number;
    }

    public long getCount(){
        return count;
    }

}
