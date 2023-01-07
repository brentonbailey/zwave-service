package com.bbailey.smarthome.zwave.api.common;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CommandSequence {

	
	
	
	public enum SequenceState {
		/**
		 * Request is queued but has not been sent yet
		 */
		PENDING,
		/**
		 * Waiting for the module to send an ACK frame
		 */
		WAITING_FOR_ACK,
		/**
		 * Waiting for the module to send a response frame
		 */
		WAITING_FOR_RESPONSE,
		/**
		 * Waiting for the module to send a callback
		 */
		WAITING_FOR_CALLBACK,
		/**
		 * Complete
		 */
		COMPLETE,
		/**
		 * The sequence has failed to complete
		 */
		FAILED
	}
	
	private final SerializableCommand request;
	private final CommandFlow flow;
	private final CountDownLatch latch;
	
	private DeserializableCommand response;
	private DeserializableCommand callback;
	
	private volatile SequenceState state = SequenceState.PENDING;
	private int numRetries = 3;
	private int attempt = 1;
	
	/**
	 * Can attempt to send the request based on the retry policy
	 * @return True if the transmission can proceed, false otherwise
	 */
	public boolean canTransmit() {
		return attempt <= numRetries;
	}
	
	/**
	 * Get whether this sequence is in a complete state
	 * @return True if complete, false if still in progress
	 */
	public boolean isComplete() {
		return state == SequenceState.COMPLETE || state == SequenceState.FAILED;
	}
	
	
	/**
	 * The transmission attempt counter
	 * @return the attempt
	 */
	public int getAttempt() {
		return attempt;
	}


	/**
	 * @return the request
	 */
	public SerializableCommand getRequest() {
		return request;
	}
	
	
	/**
	 * The expected flow of the command sequence
	 * @return The flow
	 */
	public CommandFlow getFlow() {
		return flow;
	}
	
	
	/**
	 * @return The current state of the command sequeuence
	 */
	public SequenceState getState() {
		return state;
	}
	
	
	/**
	 * Update the state
	 * @param state The new state of the sequence
	 */
	private void setState(SequenceState state) {
		this.state = state;
		if (state == SequenceState.COMPLETE || state == SequenceState.FAILED) {
			latch.countDown();
		}
	}
	
	
	/**
	 * @return the response
	 */
	public Optional<DeserializableCommand> getResponse() {
		return Optional.ofNullable(response);
	}
	
	
	/**
	 * @param response the response to set
	 */
	public void setResponse(DeserializableCommand response) {
		this.response = response;
	}
	
	
	/**
	 * @return the callback
	 */
	public Optional<DeserializableCommand> getCallback() {
		return Optional.ofNullable(callback);
	}
	
	
	/**
	 * @param callback the callback to set
	 */
	public void setCallback(DeserializableCommand callback) {
		this.callback = callback;
	}
	
	
	/**
	 * @return the numRetries
	 */
	public int getNumRetries() {
		return numRetries;
	}


	/**
	 * @param numRetries the numRetries to set
	 */
	public void setNumRetries(int numRetries) {
		this.numRetries = numRetries;
	}


	public CommandSequence(SerializableCommand request, CommandFlow flow) {
		this.request = request;
		this.flow = flow;
		this.latch = new CountDownLatch(1);
	}
	
	
	/**
	 * Start the sequence. This is called when the request is sent to the zwave stick
	 */
	public void startSequence() {
		
		if (state != SequenceState.PENDING) {
			throw new IllegalStateException("Cannot start a command sequeunce in state: " + state);
		}
		
		switch (flow) {
		case UNACK_FRAME:
			setState(SequenceState.COMPLETE);
			break;
		default:
			setState(SequenceState.WAITING_FOR_ACK);
			break;
		}
		
	}
	
	
	/**
	 * Progress the sequence by recording that we have received an ACK from 
	 * for our request
	 */
	public void acknowledgeRequest() {
		
		if (state != SequenceState.WAITING_FOR_ACK) {
			throw new IllegalStateException("Cannot acknowledge a request in state: " + state);
		}
		
		switch (flow) {
		case ACK_FRAME:
			setState(SequenceState.COMPLETE);
			break;
		case ACK_FRAME_WITH_RESPONSE:
		case ACK_FRAME_WITH_RESPONSE_AND_CALLBACK:
			setState(SequenceState.WAITING_FOR_RESPONSE);
			break;
		case ACK_FRAME_WITH_CALLBACK:
			setState(SequenceState.WAITING_FOR_CALLBACK);
			break;
		default:
			throw new IllegalStateException("State " + state + " is not valid for a flow of " + flow);
		}
	}
	
	
	/**
	 * Progress the sequence by recoding that we have received a response frame
	 * @param response The response
	 */
	public void updateResponse(Command response) {
		
		/*
		 * Allow us to jump straight to the response if we haven't received the ACK
		 */
		if (state != SequenceState.WAITING_FOR_RESPONSE && state != SequenceState.WAITING_FOR_ACK) {
			throw new IllegalStateException("Cannot update a response in state: " + state);
		}
		
		switch (flow) {
		case ACK_FRAME_WITH_RESPONSE:
			setState(SequenceState.COMPLETE);
			break;
		case ACK_FRAME_WITH_RESPONSE_AND_CALLBACK:
			setState(SequenceState.WAITING_FOR_CALLBACK);
			break;
		default:
			throw new IllegalStateException("State " + state + " is not valid for a flow of " + flow);
		}
	}
	
	
	/**
	 * Progress the sequence by recording the callback that we received from the z-wave module
	 * @param callback The callback payload
	 */
	public void updateCallback(Command callback) {
		
		if (state != SequenceState.WAITING_FOR_CALLBACK) {
			throw new IllegalStateException("Cannot update a callback in state: " + state);
		}
		
		switch (flow) {
		case ACK_FRAME_WITH_CALLBACK:
		case ACK_FRAME_WITH_RESPONSE_AND_CALLBACK:
			setState(SequenceState.COMPLETE);
			break;
		default:
			throw new IllegalStateException("State " + state + " is not valid for a flow of " + flow);
		}
	}
	
	
	public void markAsFailed() {
		setState(SequenceState.FAILED);
	}
	
	
	public void retry() {
		attempt++;
		this.state = SequenceState.PENDING;
	}
	
	
	/**
	 * Wait for the sequence to complete.
	 * @param timeout
	 * @param unit
	 */
	public boolean waitForCompletion(long timeout, TimeUnit unit) {
		try {
			return latch.await(timeout, unit);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}


	@Override
	public String toString() {
		return "CommandSequence [" + (request != null ? "request=" + request + ", " : "")
				+ (flow != null ? "flow=" + flow + ", " : "") + (state != null ? "state=" + state + ", " : "")
				+ (response != null ? "response=" + response + ", " : "")
				+ (callback != null ? "callback=" + callback : "") + "]";
	}

}
