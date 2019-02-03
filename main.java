package parsers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import javafx.util.Pair;
import parsers.mood.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by Mohamed Boudouar et oussama ait lhouari on 23/10/18.
 */
public class main {

    public static void main(String[] args) throws IOException {



    }

    static class A {

        /**
         * Checks that the given string is​​​​​​​‌‌‌‌​​‌‌​​​‌​‌‌‌​​‌​​‌​​ correct.
         */
        public static boolean check(String s) {
            //get the lenght of the String
            int length=s.length();
            // convert straing to array of characters
            char [] array=s.toCharArray();
            // if the array if empty
            if(length==0) return true;
            // we used a stack to puch the character
            Stack<Character> stack=new Stack<Character>();
            for(int i=0; i<length; i++)
            {
                if(array[i]=='(' || array[i]=='{' || array[i]=='[' )
                {
                    // we check if the character is ( or { or [
                    stack.push(array[i]);
                }
                // we check if the character is ( or { or [
                if(array[i]==')' ||array[i]=='}' ||array[i]==']')
                {
                    // we test if the stack is empty so its false
                    if(stack.isEmpty()) return false;
                    // we pop the character back to test
                    // to pop and test then
                    char temp=stack.pop();
                    if((temp=='(' && array[i]==')' ) || (temp=='{' && array[i]=='}' ) ||(temp=='[' && array[i]==']' )  )
                    {
                        continue;
                    }else
                    {
                        return false;
                    }
                }
            }
            //if its empty
            if(!stack.isEmpty()) return false;
            return true;
        }
    }
}
