package org.driangle.sm

import java.util.concurrent.atomic.AtomicReference

/**
 * A FunctionBased implementation of a simple StateMachine.
 * Allows client to specify transition using a function of type (State, Input) => State.
 * This implementation ensures that each transition is done in a thread-safe manner by using an AtomicReference and compareAndSet.
 * However, due to the fact that it is mutable, it is not inherently threadsafe.
 *
 * @param initialState the first state of the StateMachine
 * @param transition   a function that will be used to transition the StateMachine to its next state.
 *                     The arguments will be (currentState, input) and the output should be the next state the StateMachine should transition to.
 * @tparam State the type of the StateMachine's state
 * @tparam Input the type of the StateMachine's Input
 */
class FunctionBasedStateMachine[State, Input](private val initialState: State,
                                              private val transition: (State, Input) => State)
  extends StateMachine[State, Input] {

  require(transition != null, "[transition] function cannot be null")

  private val _currentState: AtomicReference[State] = new AtomicReference(initialState)

  override def currentState: State = _currentState.get()

  override def consume(input: Input): State = {
    var result = false
    var newState : State = currentState
    while (!result) {
      val currentStateRightNow = this._currentState.get()
      newState = transition.apply(currentStateRightNow, input)
      result = this._currentState.compareAndSet(currentStateRightNow, newState)
    }
    newState
  }

  override def peek(input: Input): State = transition.apply(this._currentState.get(), input)

  override def reset(): State = {
    this._currentState.set(initialState)
    initialState
  }

  override def set(newState: State): Unit = {
    this._currentState.set(newState)
  }

}

object FunctionBasedStateMachine {

  /**
   * Used to build a FunctionBasedStateMachine. You must at least provide an initial state and at least one transition.
   *
   * @tparam State the type of the State
   * @tparam Input the type of the Input
   */
  class Builder[State, Input]() {

    private type Transition = PartialFunction[(State, Input), State]

    private var initialState: Option[State] = None
    private var transitions: Seq[Transition] = List.empty

    /**
     * Sets the initial state of the StateMachine
     *
     * @param state the initial state
     * @return the same instance of FunctionBasedStateMachine.Builder
     */
    def initialState(state: State): FunctionBasedStateMachine.Builder[State, Input] = {
      initialState = Some(state)
      this
    }

    /**
     * Appends a transition to the StateMachine. If the PartialFunction is not defined for a given input then the next transition function will
     * be attempted. If no transition functions match then the StateMachine will remain in its current state.
     *
     * @param next a function to transition the StateMachine to its next state
     * @return the same instance of FunctionBasedStateMachine.Builder
     */
    def transition(next: Transition): FunctionBasedStateMachine.Builder[State, Input] = {
      require(next != null, "[next] transition function cannot be null")
      transitions = transitions :+ next
      this
    }

    /**
     * Builds a FunctionBasedStateMachine instance.
     * - You must set the initialSate.
     * - You must set at least one transition.
     *
     * @return a StateMachine
     */
    def build(): StateMachine[State, Input] = {
      if (initialState.isEmpty) {
        throw new IllegalStateException("You must set the initial state of the state machine using 'withInitialState'")
      }
      if (transitions.isEmpty) {
        throw new IllegalStateException("You must provide at least one transition")
      }
      val compositeTransition = (state : State, input : Input) => {
        transitions.filter(_.isDefinedAt(state, input)).headOption
          .map(_.apply(state, input)) match {
          case Some(next) => next
          case None => state // no transition matched input, stay in current state
        }
      }
      new FunctionBasedStateMachine(initialState.get, compositeTransition)
    }
  }

}
