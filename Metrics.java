package parsers;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Metrics {
    private File file;
    private List<CompilationUnit> cus;
    private Set<String> classes;




    public CompilationUnit findCu(String s)
    {
        return findCompilationUnit.apply(cus,s);
    }
    static BiFunction<List<CompilationUnit>,String,Integer> CBO = new BiFunction<List<CompilationUnit>, String, Integer>() {
        @Override
        public Integer apply(List<CompilationUnit> compilationUnits, String s) {
            CompilationUnit compUnit = findCompilationUnit.apply(compilationUnits,s);
            // Get the types of variables declared
            List<String> types = new LinkedList<>();
            FieldsNameVisitor visitor = new FieldsNameVisitor();
            visitor.visit(compUnit.findAll(ClassOrInterfaceDeclaration.class).get(0),types);

            // Get the List of the calls inside class's methods
            MethodCallVisitor m =  new MethodCallVisitor();
            List<MethodDeclaration> mdeclaration = compUnit.getClassByName(s).get().getMethods();
            for (MethodDeclaration method : mdeclaration ) {

                method.accept(m, null);
            }
            //Two classes are coupled when methods declared in one class use methods or instance variables
            // defined by the other class.
            List<String> allClasses = compilationUnits.stream()
                    .map(p->p.findAll(ClassOrInterfaceDeclaration.class).get(0).getNameAsString())
                    .collect(Collectors.toList());

            Integer CBO =types.stream()
                    .filter(p->allClasses.contains(p))
                    .map(c->1)
                    .reduce((x,y)->x+y)
                    .get();
            /***
             * TODO I need to SEE in the methods
             * */


            return CBO;
        }
    };


    /***
     * RFC = M + R
     * M = number of methods in the class
     * R = number of remote methods directly called by methods of the class
     ***/

    static BiFunction<CompilationUnit,String,Integer> RFC = new BiFunction<CompilationUnit, String, Integer>() {
        @Override
        public Integer apply(CompilationUnit compilationUnit, String s) {
            List<String> names = new LinkedList<>();
            // Visitor to get the number of calls of methods in one class
            MethodCallVisitor m =  new MethodCallVisitor();
            List<MethodDeclaration> mdeclaration = compilationUnit.getClassByName(s).get().getMethods();
            MethodsNamesVisitor methodsNamesVisitor =  new MethodsNamesVisitor();
            methodsNamesVisitor.visit(compilationUnit,names);
            for (MethodDeclaration method : mdeclaration ) {

                method.accept(m, null);
            }
//            System.out.println(m.getMap().toString());
            Boolean extist = m.getMap().entrySet().stream()
                    .filter(x ->
                            !names.contains(x.getKey())
                    )
                    .map(x->x.getValue())
                    .reduce((x,y)->x+y)
                    .isPresent();
            Integer extrenalCalls=0;
            if (extist) {
                extrenalCalls = m.getMap().entrySet().stream()
                        .filter(x ->
                                !names.contains(x.getKey())
                        )
                        .map(x->x.getValue())
                        .reduce((x,y)->x+y)
                        .get();
            }


            return extrenalCalls+WMC.apply(compilationUnit,s);
        }
    };


    static BiFunction<CompilationUnit,String,Integer> WMC = new BiFunction<CompilationUnit, String, Integer>() {
        @Override
        public Integer apply(CompilationUnit compilationUnit, String s) {
            //Getting the number of methods of the class
            MethodVisitor mv = new MethodVisitor();
            Integer numberOfMethods = mv.visit(compilationUnit.getClassByName(s).get(),null);
//            System.out.println(numberOfMethods);
            return  numberOfMethods;
        }
    };

    public static final BiFunction<List<CompilationUnit>,String,Integer> NOC = (compilationUnits, s) -> {
        ClassesVisitor c = new ClassesVisitor();
        int noc = compilationUnits.stream()
                .map(p->c.visit(p,null))
                .flatMap(x->x.stream())
                .map(p->p.getValue())
                .filter(p->p.contains(new ClassOrInterfaceType(null,s)))
                .collect(Collectors.toList())
                .size();
        return noc ;

    };
    public static  BiFunction<List<CompilationUnit>,String,Integer> DIT = new BiFunction<List<CompilationUnit>, String, Integer>() {
        @Override
        public Integer apply(List<CompilationUnit> compilationUnits, String s) {
            CompilationUnit cu = findCompilationUnit.apply(compilationUnits,s);// get the compilation Unit of the class
            NodeList<ClassOrInterfaceType> list =  cu.getClassByName(s)
                    .get()
                    .getExtendedTypes();// Get the extended Types of the class
            if(list.isEmpty())
                return 1;// class object
            return 1 + DIT.apply(compilationUnits, list.get(0).asString());

        }
    };

    static final BiFunction<List<CompilationUnit>,String,CompilationUnit> findCompilationUnit = (compilationUnits, ss) -> {
        CompilationUnit cu =compilationUnits.stream()
                .filter(p->p.findAll(ClassOrInterfaceDeclaration.class)
                        .get(0).getNameAsString().equals(ss))
                .findAny()
                .orElse(null);
        return cu;
    };
    public void getClasses()
    {
        for(CompilationUnit classe: cus){
            classes.add(classe.findAll(ClassOrInterfaceDeclaration.class).get(0).getNameAsString());

        }
    }
    public int getTotalMetrics()
    {
        int metrics =0;
       for (CompilationUnit cu : cus)
           metrics+=getMetricsSum(cu.findAll(ClassOrInterfaceDeclaration.class).get(0).getNameAsString());
       return metrics;
    }
    public int getMetricsSum(String classe) {
        int sum = 0;
            sum += NOC.apply(cus,classe);
            sum += DIT.apply(cus,classe);
            sum += RFC.apply(findCompilationUnit.apply(cus,classe),classe);

        return sum;
    }

    public Metrics(File file) {
        this.file = file;
    }
    public Metrics(String path) throws IOException {
        File dir = new File(
                path);
        CombinedTypeSolver typeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(dir));
        ParserConfiguration parserConfiguration =
                new ParserConfiguration()
                        .setSymbolResolver(new JavaSymbolSolver(typeSolver));

        SourceRoot sourceRoot = new
                SourceRoot(dir.toPath());
        sourceRoot.setParserConfiguration(parserConfiguration);
        List<ParseResult<CompilationUnit>> parseResults =
                sourceRoot.tryToParse("");


        // For computing the metrics, we need to have an access to all the classes.
        // @variable allCus = All computation Units of the packages, and foreach class we create an AST.
        this.cus = parseResults.stream()
                .filter(ParseResult::isSuccessful)
                .map(r -> r.getResult().get())
                .collect(Collectors.toList());
    }

    public List<CompilationUnit> getCus() {
        return cus;
    }
}
