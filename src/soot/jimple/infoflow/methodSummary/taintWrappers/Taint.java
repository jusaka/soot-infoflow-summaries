package soot.jimple.infoflow.methodSummary.taintWrappers;

import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;

/**
 * Class representing a tainted item during propagation
 * 
 * @author Steven Arzt
 *
 */
class Taint extends FlowSink implements Cloneable {

	public Taint(SourceSinkType type, int paramterIdx, String baseType,
			boolean taintSubFields) {
		super(type, paramterIdx, baseType, taintSubFields);
	}
	
	public Taint(SourceSinkType type, int paramterIdx,
			String baseType, String[] fields, String[] fieldTypes,
			boolean taintSubFields) {
		super(type, paramterIdx, baseType, fields, fieldTypes, taintSubFields);
	}
	
	public Taint(SourceSinkType type, int paramterIdx,
			String baseType, String[] fields, String[] fieldTypes,
			boolean taintSubFields, GapDefinition gap) {
		super(type, paramterIdx, baseType, fields, fieldTypes, taintSubFields, gap);
	}
	
	@Override
	public Taint clone() {
		return new Taint(getType(), getParameterIndex(), getBaseType(),
				getAccessPath(), getAccessPathTypes(), taintSubFields());
	}
	
}