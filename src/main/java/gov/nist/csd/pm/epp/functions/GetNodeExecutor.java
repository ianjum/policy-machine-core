package gov.nist.csd.pm.epp.functions;

import gov.nist.csd.pm.epp.FunctionEvaluator;
import gov.nist.csd.pm.epp.events.EventContext;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;
import gov.nist.csd.pm.pip.graph.model.nodes.NodeType;
import gov.nist.csd.pm.pip.obligations.model.functions.Arg;
import gov.nist.csd.pm.pip.obligations.model.functions.Function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetNodeExecutor implements FunctionExecutor {
    @Override
    public String getFunctionName() {
        return "get_node";
    }

    @Override
    public int numParams() {
        return 2;
    }

    @Override
    public Node exec(EventContext eventCtx, String user, String process, PDP pdp, Function function, FunctionEvaluator functionEvaluator) throws PMException {
        List<Arg> args = function.getArgs();
        if (args == null || args.size() < numParams() || args.size() > numParams()) {
            throw new PMException(getFunctionName() + " expected at least two arguments (name and type) but found none");
        }

        // first arg should be a string or a function tht returns a string
        Arg arg = args.get(0);
        String name = arg.getValue();
        if(arg.getFunction() != null) {
            name = functionEvaluator.evalString(eventCtx, user, process, pdp, arg.getFunction());
        }

        // second arg should be the type of the node to search for
        arg = args.get(1);
        String type = arg.getValue();
        if(arg.getFunction() != null) {
            type = functionEvaluator.evalString(eventCtx, user, process, pdp, arg.getFunction());
        }

        Map<String, String> props = new HashMap<>();
        if(args.size() > 2) {
            arg = args.get(2);
            if (arg.getFunction() != null) {
                props = (Map) functionEvaluator.evalMap(eventCtx, user, process, pdp, arg.getFunction());
            }
        }

        if (name != null) {
            return pdp.getPAP().getGraphPAP().getNode(name);
        }

        Set<Node> search = pdp.getPAP().getGraphPAP().search(NodeType.toNodeType(type), props);
        return search.iterator().next();
    }
}
