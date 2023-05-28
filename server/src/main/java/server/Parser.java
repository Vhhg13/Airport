package server;

import java.util.LinkedList;

public class Parser {

    public static void main(String[] args) {
        String[] commands = {
                "echo hello",
                "echo",
                "echo 'abc\"",
                " \"echo abc\" def\"gh i\"",
                "echo \"abc def",
                "\"\" \"\""
        };
        for(String str : commands){
            System.out.println("Command: " + str);
            LinkedList<String> cmdlets = parse(str);
            System.out.printf("Array(%d){\n", cmdlets.size());
            for(String cmdlet : cmdlets){
                System.out.println("\t" + cmdlet);
            }
            System.out.println("}");
        }
    }
    private enum State{
        ST,
        WH,
        APD,
        APDQ

    }
    public static LinkedList<String> parse(String cmd){
        LinkedList<String> res = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        State state = State.ST;
        for(char i=0;i<cmd.length()+1;++i){
            char ch;
            if(i==cmd.length()) ch = 0;
            else ch = cmd.charAt(i);
            switch(state){
                case ST:
                    if(ch==' ') continue;
                    if(ch=='\'' || ch=='\"')
                        state=State.APDQ;
                    else {
                        sb.append(ch);
                        state = State.APD;
                    }
                    break;
                case APD:
                    if(ch==0){
                        res.add(sb.toString());
                        break;
                    }
                    if(ch=='\"' || ch=='\''){
                        state=State.APDQ;
                    }else if(ch==' ') {
                        state = State.WH;
                        res.add(sb.toString());
                    }else
                        sb.append(ch);
                    break;
                case APDQ:
                    if(ch==0){
                        res.add(sb.toString());
                        break;
                    }
                    if(ch=='\"' || ch=='\''){
                        state=State.APD;
                    }else{
                        sb.append(ch);
                    }
                    break;
                case WH:
                    if(ch==' ' || ch==0) continue;
                    sb = new StringBuilder();
                    if(ch=='\"' || ch=='\''){
                        state=State.APDQ;
                    }else {
                        sb.append(ch);
                        state=State.APD;
                    }
                    break;
                default:
            }
        }
        return res;
    }
}
