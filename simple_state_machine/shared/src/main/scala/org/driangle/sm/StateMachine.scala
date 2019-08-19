package org.driangle.sm

/**
 * Represents a simple StateMachine with a minimal API
 *
 * @tparam State the type of the StateMachine's state
 * @tparam Input the type of the StateMachine's Input
 */
trait StateMachine[State, Input] {

  /**
   * Obtains the current state of the StateMachine.
   * If this is is called before consuming any input then it represents the initial state.
   *
   * @return the current state of the StateMachine
   */
  def currentState: State

  /**
   * Consumes the input and attempts to advance the StateMachine to the next state if any transitions match the input.
   * If no transitions match the input, the state machine should remain in its currentState.
   *
   * @param input the input to consume
   * @return the next state after consuming the input
   */
  def consume(input: Input): State

  /**
   * Allows clients to foresee the next State the StateMachine would be in if the input provided was consumed.
   * This operation does not modify the StateMachine's internal state.
   *
   * @param input the input to preview
   * @return the state the StateMachine would be in if the input was consumed
   */
  def peek(input: Input): State

  /**
   * Resets the StateMachine back to its initial state
   *
   * @return the StateMachine's initial state
   */
  def reset(): State

  /**
   * Forcefully sets the state of the StateMachine.
   *
   * @param newState the state to set
   */
  def set(newState: State): Unit

}

object StateMachine {
  def WithFunctionTransitions[State, Input]() : FunctionBasedStateMachine.Builder[State, Input] = {
    new FunctionBasedStateMachine.Builder[State, Input]()
  }
}