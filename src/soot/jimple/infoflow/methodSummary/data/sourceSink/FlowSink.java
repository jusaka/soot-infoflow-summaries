package soot.jimple.infoflow.methodSummary.data.sourceSink;

import java.util.Arrays;
import java.util.Map;

import soot.jimple.infoflow.methodSummary.data.summary.SourceSinkType;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;
import soot.jimple.spark.summary.GapDefinition;

/**
 * Representation of a flow sink.
 * 
 * @author Steven Arzt
 */
public class FlowSink extends AbstractFlowSinkSource {
	private final boolean taintSubFields;
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			String baseType, String[] fields, String[] fieldTypes,
			boolean taintSubFields) {
		super(type, paramterIdx, baseType, fields, fieldTypes);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			String baseType, String[] fields, String[] fieldTypes,
			boolean taintSubFields, GapDefinition gap) {
		super(type, paramterIdx, baseType, fields, fieldTypes, gap);
		this.taintSubFields = taintSubFields;
	}

	public FlowSink(SourceSinkType type, int paramterIdx,
			String baseType, String[] fields, String[] fieldTypes,
			boolean taintSubFields, GapDefinition gap,
			Object userData) {
		super(type, paramterIdx, baseType, fields, fieldTypes, gap, userData);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			String baseType, boolean taintSubFields) {
		super(type, paramterIdx, baseType, null, null);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			String baseType, boolean taintSubFields, GapDefinition gap) {
		super(type, paramterIdx, baseType, null, null, gap);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			String baseType, boolean taintSubFields, GapDefinition gap,
			Object userData) {
		super(type, paramterIdx, baseType, null, null, gap, userData);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, String baseType, String[] accessPath,
			String[] accessPathTypes, boolean taintSubFields2) {
		super(type, -1, baseType, accessPath, accessPathTypes);
		this.taintSubFields = taintSubFields2 || (accessPath != null
				&& accessPath.length > this.accessPath.length);
	}

	public FlowSink(SourceSinkType type, String baseType, String[] accessPath,
			String[] accessPathTypes, boolean taintSubFields2, GapDefinition gap) {
		super(type, -1, baseType, accessPath, accessPathTypes, gap);
		this.taintSubFields = taintSubFields2 || (accessPath != null
				&& accessPath.length > this.accessPath.length);
	}
	
	public boolean taintSubFields(){
		return taintSubFields;
	}

	/**
	 * Checks whether the current source or sink is coarser than the given one,
	 * i.e., if all elements referenced by the given source or sink are also
	 * referenced by this one
	 * @param src The source or sink with which to compare the current one
	 * @return True if the current source or sink is coarser than the given one,
	 * otherwise false
	 */
	@Override
	public boolean isCoarserThan(AbstractFlowSinkSource other) {
		return super.isCoarserThan(other)
				&& other instanceof FlowSink
				&& this.taintSubFields;
	}
	
	@Override
	public Map<String, String> xmlAttributes() {
		Map<String, String> res = super.xmlAttributes();
		res.put(XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS, taintSubFields() + "");
		return res;
	}
	
	@Override
	public String toString(){
		if (isGapBaseObject() && getGap() != null)
			return "Gap base for " + getGap().getSignature();
		
		String gapString = getGap() == null ? ""
				: "Gap " + getGap().getSignature() + " ";
		
		if (isParameter())
			return gapString
					+ "Parameter " + getParameterIndex() + (accessPath == null ? "" : " "
					+ Arrays.toString(accessPath)) + " " + taintSubFields();
		
		if (isField())
			return gapString
					+ "Field" + (accessPath == null ? "" : " "
					+ Arrays.toString(accessPath)) + " " + taintSubFields();
		
		if(isReturn())
			return gapString
					+ "Return" + (accessPath == null ? "" : " "
					+ Arrays.toString(accessPath)) + " " + taintSubFields();
		
		if (isCustom())
			return "CUSTOM " + gapString
					+ "Parameter " + getParameterIndex() + (accessPath == null ? "" : " "
					+  Arrays.toString(accessPath));
		
		return "invalid sink";
	}	
	
	@Override
	public int hashCode() {
		return super.hashCode() + (31 * (taintSubFields ? 1 : 0));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		
		return this.taintSubFields == ((FlowSink) obj).taintSubFields;
	}
	
	/**
	 * Validates this flow sink
	 * @param methodName The name of the containing method. This will be used to
	 * give more context in exception messages
	 */
	public void validate(String methodName) {
		if (getType() == SourceSinkType.GapBaseObject && getGap() == null)
			throw new RuntimeException("Gap base flows must always be linked "
					+ "with gaps. Offending method: " + methodName);
	}
}
