package parsers;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.HashMap;
import java.util.Map;

public class MethodCallVisitor extends VoidVisitorAdapter<Integer> {
    public static int count = 0;
    private Map<String,Integer> map = new HashMap<>();
    @Override
    public void visit(MethodCallExpr n, Integer arg) {
        count++;
        Integer i=map.get(n.getNameAsString());
        if (i == null ) map.put(n.getNameAsString(),1);
        else map.put(n.getNameAsString(),i+1);

    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}