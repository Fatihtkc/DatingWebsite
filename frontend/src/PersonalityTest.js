import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Navigate } from "react-router-dom";
import { useLocation } from 'react-router-dom';
import './css/style.css';

const questions = [
  { question: "How would you describe yourself?", options: ["Extroverted and social", "Balanced, sometimes introverted, sometimes extroverted", "Introverted and calm"] },
  { question: "How do you handle stress?", options: ["By doing sports or physical activities", "By listening to music or watching movies", "By meditating or spending time alone"] },
  { question: "How do you feel about meeting new people?", options: ["Very easy, I start conversations right away", "Moderate, I observe first", "Difficult, because I'm shy"] },
  { question: "What makes you happiest on a weekend?", options: ["Going out with friends", "Staying home with books and movies", "Going on a solo nature trip"] },
  { question: "How do you like spending time with your partner?", options: ["Going out (traveling, concerts, dining out)", "Spending cozy time at home (movies, cooking, relaxing)", "A mix of both, depending on the mood"] },
  { question: "What do you value most in a relationship?", options: ["Trust and loyalty", "Fun and harmony", "Communication and understanding"] },
  { question: "How do you prefer your partner to show affection?", options: ["Through small surprises", "By spending quality time together", "Through verbal expressions of love"] },
  { question: "How do you react during an argument?", options: ["I want to solve it immediately by talking", "I take a break to cool down before discussing", "I withdraw and need time alone"] },
  { question: "Which lifestyle suits you better?", options: ["Adventurous and spontaneous", "Planned and organized", "A balance between the two"] },
  { question: "What kind of relationship do you envision for the future?", options: ["Long-term and serious", "Living in the moment, I go with the flow", "Not sure, getting to know someone is important"] },
  { question: "How do you feel about pets?", options: ["Love them, I have or want one", "Like them, but not sure about having one", "Not really interested"] },
  { question: "What's your ideal vacation?", options: ["Relaxing on a beach", "Exploring cities and sightseeing", "Being in nature, camping or hiking"] },
  { question: "Which activities interest you the most?", options: ["Art and culture (music, theater, museums)", "Sports and outdoor activities (swimming, hiking, cycling)", "Tech and gaming (computers, consoles)"] },
  { question: "What do you usually do in your free time?", options: ["Read books or watch movies", "Hang out with friends", "Learn new things or explore hobbies"] }
];

const PersonalityTest = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState({});
  const totalQuestions = questions.length;

  if (!location.state || !location.state.fromSignup) {
    return <Navigate to="/index" />;
  }

  const user = location.state?.user;

  const handleSignIn = () => {
    navigate('/login'); 
  };

  const handleSelect = (option) => {
    setSelectedAnswers((prev) => ({ ...prev, [currentQuestion]: option }));
    if (currentQuestion < totalQuestions - 1) {
      setCurrentQuestion(currentQuestion + 1);
    } else {
      navigate("/PersonalInformation", { state: { user, fromTest:true } });
    }
  };

  const handlePrevious = () => {
    if (currentQuestion > 0) {
      setCurrentQuestion(currentQuestion - 1);
    }
  };

  const handleLogoClick = () => {
    navigate("/index"); 
  };

  return (
    <>
    <header>
        <div className="logo">
          <h1 className="logo-text" onClick={handleLogoClick} style={{ cursor: "pointer" }}>SoulM</h1>
        </div>
        <button className="signin-button" onClick={handleSignIn}>
          Sign In
        </button>
      </header>

    <div className="page-container2">
      <div className="container2">
        <h1 className="title">Personality Test</h1>

        <div className="progress-bar-container">
          <div
            className="progress-bar"
            style={{ width: `${((currentQuestion + 1) / totalQuestions) * 100}%` }}
          ></div>
        </div>

        <h2 className="question-text" style={{ color: 'darkred' }}>
          {questions[currentQuestion].question}
        </h2>

        <div className="options-container">
          {questions[currentQuestion].options.map((option, index) => (
            <button key={index} className="option-button" onClick={() => handleSelect(option)}>
              {option}
            </button>
          ))}
        </div>

        <div className="navigation-container">
          <button onClick={handlePrevious} disabled={currentQuestion === 0} className="nav-button">
            ◀ Previous
          </button>
          <button disabled className="nav-button">
            {currentQuestion + 1} / {totalQuestions}
          </button>
        </div>
      </div>
    </div>
    <div className="footer-summary">
        &copy; SoulM.com | Designed by Group 19
     </div>
    </>
  );
};

export default PersonalityTest;
