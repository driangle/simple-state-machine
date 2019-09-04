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
 * @param onChangeCallback a callback that will be invoked whenever the state changes
 * @tparam State the type of the StateMachine's state
 * @tparam Input the type of the StateMachine's Input
 */
class FunctionBasedStateMachine[State, Input](private val initialState: State,
                                              private val transition: (State, Input) => State,
                                              private val onChangeCallback : Function2[State, State, Unit] = (_: State, _ : Input) => {})
  extends StateMachine[State, Input] {

  require(transition != null, "[transition] function cannot be null")
  require(onChangeCallback != null, "[onChangeCallback] cannot be null")

  private val _currentState: AtomicReference[State] = new AtomicReference(initialState)

  override def currentState: State = _currentState.get()

  override def consume(input: Input): State = {
    val startState = currentState
    var result = false
    var newState : State = startState
    while (!result) {
      val currentStateRightNow = this._currentState.get()
      newState = transition.apply(currentStateRightNow, input)
      result = this._currentState.compareAndSet(currentStateRightNow, newState)
    }
    if (startState != newState) {
      onChangeCallback.apply(startState, newState)
    }
    newState
  }

  override def peek(input: Input): State = transition.apply(this._currentState.get(), input)

  override def reset(): State = {
    val startState = currentState
    this._currentState.set(initialState)
    if (startState != initialState) {
      onChangeCallback.apply(startState, initialState)
    }
    initialState
  }

  override def set(newState: State): Unit = {
    val startState = currentState
    this._currentState.set(newState)
    if (startState != initialState) {
      onChangeCallback.apply(startState, newState)
    }
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
    private type OnChangeCallback = Function2[State, State, Unit]

    private var initialState: Option[State] = None
    private var transitions: Seq[Transition] = List.empty
    private var onChangeCallbacks : Seq[OnChangeCallback] = List.empty

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

    def onChange(callback : OnChangeCallback) : FunctionBasedStateMachine.Builder[State, Input] = {
      require(callback != null, "[callback] cannot be null")
      onChangeCallbacks = onChangeCallbacks :+ callback
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
      val compositeOnChangeCallback = (lastState : State, newState : State) => {
        onChangeCallbacks.foreach(_.apply(lastState, newState))
      }
      new FunctionBasedStateMachine(initialState.get, compositeTransition, compositeOnChangeCallback)
    }
  }

}
