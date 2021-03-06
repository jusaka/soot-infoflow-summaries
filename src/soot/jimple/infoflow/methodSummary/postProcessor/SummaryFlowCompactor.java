package soot.jimple.infoflow.methodSummary.postProcessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.infoflow.methodSummary.data.summary.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.spark.summary.GapDefinition;

/**
 * Class for compacting a set of method flow summaries
 * 
 * @author Steven Arzt
 *
 */
public class SummaryFlowCompactor {
	
	private static final Logger logger = LoggerFactory.getLogger(InfoflowResultPostProcessor.class);
	
	private final MethodSummaries summaries;
	
	/**
	 * Creates a new instance of the SummaryFlowCompactor class
	 * @param summaries The set of flow summaries to compact
	 */
	public SummaryFlowCompactor(MethodSummaries summaries) {
		this.summaries = summaries;
	}
	
	/**
	 * Compacts the flow set
	 */
	public void compact() {
		compactFlowSet();
		removeDuplicateFlows();
	}
	
	/**
	 * Compacts the flow set by removing flows that are over-approximations of
	 * others
	 * @param flows The flow set to compact
	 */
	private void compactFlowSet() {
		int flowsRemoved = 0;
		boolean hasChanged = false;
		do {
			hasChanged = false;
			for (Iterator<MethodFlow> flowIt = summaries.iterator(); flowIt.hasNext(); ) {
				MethodFlow flow = flowIt.next();
				
				// Check if there is a more precise flow
				for (MethodFlow flow2 : summaries)
					if (flow != flow2 && flow.isCoarserThan(flow2)) {
						flowIt.remove();
						flowsRemoved++;
						hasChanged = true;
						break;
					}
				
				if (hasChanged)
					break;
			}
		} while (hasChanged);
		
		logger.info("Removed {} flows in favour of more precise ones", flowsRemoved);
	}
	
	
	/**
	 * Removes duplicate flows from the given set of method summaries. A flow is
	 * considered  duplicate if the same flow already exists in reverse and is
	 * an alias relationship, i.e., a flow that is valid in both directions.
	 * @param summaries The set of summaries to clean up
	 */
	private void removeDuplicateFlows() {
		outer : for (Iterator<MethodFlow> flowIt = summaries.iterator(); flowIt.hasNext(); ) {
			MethodFlow curFlow = flowIt.next();
			
			// Check for the same flow in reverse
			for (MethodFlow compFlow : summaries) {
				if (curFlow != compFlow
						&& compFlow.isAlias()
						&& curFlow.isAlias()) {
					if (curFlow.reverse().equals(compFlow)) {
						// To make the results is reproducible, we introduce some
						// rules on which flows we keep and which ones we delete
						if (curFlow.source().getGap() == null
								&& curFlow.sink().getGap() != null)
							continue;
						if (curFlow.source().getGap() == null
								&& curFlow.sink().getGap() == null
								&& compare(curFlow.source().getAccessPath(),
										compFlow.source().getAccessPath()) > 0)
							continue;
						
						flowIt.remove();
						continue outer;
					}
				}
			}
		}
	}
	
	/**
	 * Compares two access paths to create some sort of ordering. This is
	 * important to get reproducible test cases for those flows that can be
	 * represented ambiguously.
	 * @param accessPath The first access path
	 * @param accessPath2 The second access path
	 * @return An ordering (-1, 0, 1) on the given access paths
	 */
	private int compare(String[] accessPath, String[] accessPath2) {
		if (accessPath == accessPath2)
			return 0;
		if (accessPath == null)
			return -1;
		if (accessPath2 == null)
			return 1;
		
		if (accessPath.length > accessPath2.length)
			return -1;
		return Arrays.toString(accessPath).compareTo(Arrays.toString(accessPath2));
	}

}
