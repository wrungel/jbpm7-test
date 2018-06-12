import React, { Component} from "react";
import "./App.css";

class App extends Component {
    componentDidMount() {
    fetch('http://127.0.0.1:8080/globecs-glassfishasyncwar-HEAD-2.6.10-MFROLOV-SNAPSHOT/async/brcab/0MAX00001A')
    .then(result=>result.json())
    //.then(items=>this.setState({ name: data.bookingNo }))
    .then(items=>this.setState({ name: "Uschi" }))

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
