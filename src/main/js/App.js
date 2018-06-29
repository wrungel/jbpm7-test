import React, { Component} from "react";
import "./App.css";

class App extends Component {
    componentDidMount() {
    this.setState({ name: "Uschi" })
	}

  render(){
    return(
      <div className="App">
        <h1> Hello, {this.state.name}!</h1>
      </div>
    );
  }
}

export default App;
