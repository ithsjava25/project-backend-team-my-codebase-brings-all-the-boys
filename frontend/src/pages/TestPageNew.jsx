import { useState, useRef, useEffect } from "react";

export default function TestPageNew() {
  const [count, setCount] = useState(0);
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [selectedOption, setSelectedOption] = useState("option1");
  const [isChecked, setIsChecked] = useState(false);
  const [showAlert, setShowAlert] = useState(true);

  const [activeTab, setActiveTab] = useState("tab1");
  const [openAccordion, setOpenAccordion] = useState(null);
  const [toggleOn, setToggleOn] = useState(false);
  const [toggleOn2, setToggleOn2] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [selectedDate, setSelectedDate] = useState("");
  const [showSkeleton, setShowSkeleton] = useState(true);

  const searchRef = useRef(null);
  const modalRef = useRef(null);

  useEffect(() => {
    setShowSkeleton(false);
  }, 3000);

  useEffect(() => {
    function handleClickOutside(event) {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
      if (modalRef.current && !modalRef.current.contains(event.target)) {
        setShowModal(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const accordionItems = [
    { id: 1, title: "Vad är Tailwind CSS?", content: "Tailwind CSS är ett utility-first CSS framework som låter dig bygga snabbt med direkt i din HTML." },
    { id: 2, title: "Varför använda v4?", content: "Tailwind v4 har en ny Rust-baserad motor som är 5x snabbare och stöder modern CSS som container queries och P3 färger." },
    { id: 3, title: "Hur fungerar det?", content: "Du skriver utility classes direkt i dina JSX element. Tailwind genererar sedan optimerad CSS baserat på vad du använder." },
  ];

  const suggestions = [
    { id: 1, text: "React", emoji: "⚛️" },
    { id: 2, text: "Vue", emoji: "💚" },
    { id: 3, text: "Angular", emoji: "🅰️" },
    { id: 4, text: "Svelte", emoji: "🔥" },
    { id: 5, text: "Next.js", emoji: "▲" },
  ];

  const filteredSuggestions = suggestions.filter(s => 
    s.text.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="max-w-6xl mx-auto space-y-8">

        <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Tailwind v4 Komponenter
          </h1>
          <p className="text-gray-600 text-lg">
            Interaktiva komponenter med ren Tailwind + React logik
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">

          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">📝 Formulär</h2>

            <div className="space-y-5">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  E-post
                </label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="din@email.se"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Meddelande
                </label>
                <textarea
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  placeholder="Skriv ditt meddelande..."
                  rows={4}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all resize-none"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Välj alternativ
                </label>
                <select
                  value={selectedOption}
                  onChange={(e) => setSelectedOption(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all bg-white"
                >
                  <option value="option1">Alternativ 1</option>
                  <option value="option2">Alternativ 2</option>
                  <option value="option3">Alternativ 3</option>
                </select>
              </div>

              <div className="flex items-center gap-3">
                <input
                  type="checkbox"
                  checked={isChecked}
                  onChange={(e) => setIsChecked(e.target.checked)}
                  className="w-5 h-5 rounded border-gray-300 text-blue-600 focus:ring-2 focus:ring-blue-500"
                />
                <label className="text-sm font-medium text-gray-700">
                  Godkänn villkor
                </label>
              </div>

              <div className="flex gap-3 pt-2">
                <button className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors">
                  Skicka
                </button>
                <button className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-3 px-6 rounded-lg transition-colors">
                  Avbryt
                </button>
              </div>
            </div>
          </div>

          <div className="space-y-8">
            <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
              <h2 className="text-2xl font-bold text-gray-900 mb-6">🔔 Alerts</h2>

              <div className="space-y-4">
                {showAlert && (
                  <div className="flex items-start gap-3 p-4 bg-blue-50 border-l-4 border-blue-600 rounded-r-lg">
                    <div className="text-blue-600 mt-0.5">
                      <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                      </svg>
                    </div>
                    <div className="flex-1">
                      <p className="text-sm font-semibold text-blue-900">Information</p>
                      <p className="text-sm text-blue-800 mt-1">Detta är ett informationsmeddelande med bra styling.</p>
                    </div>
                    <button onClick={() => setShowAlert(false)} className="text-blue-600 hover:text-blue-800">
                      <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                      </svg>
                    </button>
                  </div>
                )}

                <div className="flex items-start gap-3 p-4 bg-green-50 border-l-4 border-green-600 rounded-r-lg">
                  <div className="text-green-600 mt-0.5">
                    <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <p className="text-sm font-semibold text-green-900">Framgång</p>
                    <p className="text-sm text-green-800 mt-1">Åtgärden slutfördes framgångsrikt!</p>
                  </div>
                </div>

                <div className="flex items-start gap-3 p-4 bg-red-50 border-l-4 border-red-600 rounded-r-lg">
                  <div className="text-red-600 mt-0.5">
                    <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <p className="text-sm font-semibold text-red-900">Fel</p>
                    <p className="text-sm text-red-800 mt-1">Något gick fel. Försök igen.</p>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
              <h2 className="text-2xl font-bold text-gray-900 mb-6">🏷️ Badges & Tags</h2>

              <div className="flex flex-wrap gap-3">
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                  Nyhet
                </span>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                  Aktiv
                </span>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                  Varning
                </span>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
                  Kritisk
                </span>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-purple-100 text-purple-800">
                  Beta
                </span>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-800">
                  Arkiverad
                </span>
              </div>
            </div>

            <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
              <h2 className="text-2xl font-bold text-gray-900 mb-6">📊 Progress</h2>

              <div className="space-y-5">
                <div>
                  <div className="flex justify-between mb-2">
                    <span className="text-sm font-medium text-gray-700">Laddar...</span>
                    <span className="text-sm font-medium text-gray-500">75%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3">
                    <div className="bg-blue-600 h-3 rounded-full transition-all" style={{ width: '75%' }}></div>
                  </div>
                </div>

                <div>
                  <div className="flex justify-between mb-2">
                    <span className="text-sm font-medium text-gray-700">Uppladdning</span>
                    <span className="text-sm font-medium text-gray-500">45%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3">
                    <div className="bg-green-600 h-3 rounded-full transition-all" style={{ width: '45%' }}></div>
                  </div>
                </div>

                <div>
                  <div className="flex justify-between mb-2">
                    <span className="text-sm font-medium text-gray-700">Bearbetning</span>
                    <span className="text-sm font-medium text-gray-500">90%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3">
                    <div className="bg-purple-600 h-3 rounded-full transition-all" style={{ width: '90%' }}></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
            <div className="flex items-center gap-4 mb-4">
              <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">1,234</p>
                <p className="text-sm text-gray-500">Besökare</p>
              </div>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <span className="text-green-600 font-semibold">↑ 12%</span>
              <span className="text-gray-500">från förra månaden</span>
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
            <div className="flex items-center gap-4 mb-4">
              <div className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
                <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">$45,678</p>
                <p className="text-sm text-gray-500">Intäkter</p>
              </div>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <span className="text-green-600 font-semibold">↑ 23%</span>
              <span className="text-gray-500">från förra månaden</span>
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
            <div className="flex items-center gap-4 mb-4">
              <div className="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center">
                <svg className="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">89</p>
                <p className="text-sm text-gray-500">Användare</p>
              </div>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <span className="text-red-600 font-semibold">↓ 3%</span>
              <span className="text-gray-500">från förra månaden</span>
            </div>
          </div>
        </div>

        <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200 overflow-hidden">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">📋 Tabell</h2>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-4 font-semibold text-gray-700">Namn</th>
                  <th className="text-left py-3 px-4 font-semibold text-gray-700">E-post</th>
                  <th className="text-left py-3 px-4 font-semibold text-gray-700">Roll</th>
                  <th className="text-left py-3 px-4 font-semibold text-gray-700">Status</th>
                  <th className="text-left py-3 px-4 font-semibold text-gray-700">Åtgärd</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4 font-medium text-gray-900">Anna Andersson</td>
                  <td className="py-3 px-4 text-gray-600">anna@example.com</td>
                  <td className="py-3 px-4 text-gray-600">Admin</td>
                  <td className="py-3 px-4">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                      Aktiv
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <button className="text-blue-600 hover:text-blue-800 font-medium text-sm">Redigera</button>
                  </td>
                </tr>
                <tr className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4 font-medium text-gray-900">Johan Johansson</td>
                  <td className="py-3 px-4 text-gray-600">johan@example.com</td>
                  <td className="py-3 px-4 text-gray-600">Användare</td>
                  <td className="py-3 px-4">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                      Inaktiv
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <button className="text-blue-600 hover:text-blue-800 font-medium text-sm">Redigera</button>
                  </td>
                </tr>
                <tr className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4 font-medium text-gray-900">Maria Karlsson</td>
                  <td className="py-3 px-4 text-gray-600">maria@example.com</td>
                  <td className="py-3 px-4 text-gray-600">Användare</td>
                  <td className="py-3 px-4">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                      Aktiv
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <button className="text-blue-600 hover:text-blue-800 font-medium text-sm">Redigera</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">

          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">🔽 Accordion</h2>

            <div className="space-y-3">
              {accordionItems.map((item) => (
                <div key={item.id} className="border border-gray-200 rounded-lg overflow-hidden">
                  <button
                    onClick={() => setOpenAccordion(openAccordion === item.id ? null : item.id)}
                    className="w-full px-4 py-3 flex items-center justify-between bg-gray-50 hover:bg-gray-100 transition-colors"
                  >
                    <span className="font-semibold text-gray-900">{item.title}</span>
                    <svg 
                      className={`w-5 h-5 text-gray-600 transition-transform ${openAccordion === item.id ? 'rotate-180' : ''}`}
                      fill="none" 
                      stroke="currentColor" 
                      viewBox="0 0 24 24"
                    >
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                  </button>
                  {openAccordion === item.id && (
                    <div className="px-4 py-3 bg-white text-gray-600 text-sm">
                      {item.content}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">🔘 Toggle Switches</h2>

            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-semibold text-gray-900">Dark Mode</p>
                  <p className="text-sm text-gray-500">Aktivera mörkt tema</p>
                </div>
                <button
                  onClick={() => setToggleOn(!toggleOn)}
                  className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${toggleOn ? 'bg-blue-600' : 'bg-gray-300'}`}
                >
                  <span 
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition ${toggleOn ? 'translate-x-6' : 'translate-x-1'}`} 
                  />
                </button>
              </div>

              <div className="flex items-center justify-between">
                <div>
                  <p className="font-semibold text-gray-900">Notiser</p>
                  <p className="text-sm text-gray-500">Ta emot push-notiser</p>
                </div>
                <button
                  onClick={() => setToggleOn2(!toggleOn2)}
                  className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${toggleOn2 ? 'bg-green-600' : 'bg-gray-300'}`}
                >
                  <span 
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition ${toggleOn2 ? 'translate-x-6' : 'translate-x-1'}`} 
                  />
                </button>
              </div>

              <div className="flex items-center justify-between">
                <div>
                  <p className="font-semibold text-gray-900">Auto-save</p>
                  <p className="text-sm text-gray-500">Spara automatiskt</p>
                </div>
                <button
                  onClick={() => setToggleOn(!toggleOn)}
                  className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${toggleOn ? 'bg-purple-600' : 'bg-gray-300'}`}
                >
                  <span 
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition ${toggleOn ? 'translate-x-6' : 'translate-x-1'}`} 
                  />
                </button>
              </div>
            </div>
          </div>

        </div>

        <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">📑 Tabs Navigation</h2>

          <div className="border-b border-gray-200 mb-6">
            <nav className="flex gap-8">
              {[
                { id: 'tab1', label: 'Översikt', icon: '📊' },
                { id: 'tab2', label: 'Inställningar', icon: '⚙️' },
                { id: 'tab3', label: 'Analytics', icon: '📈' },
                { id: 'tab4', label: 'Dokumentation', icon: '📚' },
              ].map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`pb-4 px-1 font-semibold transition-colors relative ${
                    activeTab === tab.id 
                      ? 'text-blue-600' 
                      : 'text-gray-500 hover:text-gray-700'
                  }`}
                >
                  {tab.icon} {tab.label}
                  {activeTab === tab.id && (
                    <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-t-full"></span>
                  )}
                </button>
              ))}
            </nav>
          </div>

          <div className="space-y-4">
            {activeTab === 'tab1' && (
              <div className="bg-gray-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Översikt</h3>
                <p className="text-gray-600">Här ser du en sammanfattning av all din data och statistik. Använd dashboarden för att få snabb insikt.</p>
              </div>
            )}
            {activeTab === 'tab2' && (
              <div className="bg-gray-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Inställningar</h3>
                <p className="text-gray-600">Konfigurera ditt konto, notiser och preferenser här. Alla ändringar sparas automatiskt.</p>
              </div>
            )}
            {activeTab === 'tab3' && (
              <div className="bg-gray-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Analytics</h3>
                <p className="text-gray-600">Detaljerad analys av dina besökare, konverteringar och användarbeteende. Exportera rapporter som PDF eller CSV.</p>
              </div>
            )}
            {activeTab === 'tab4' && (
              <div className="bg-gray-50 p-6 rounded-lg">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Dokumentation</h3>
                <p className="text-gray-600">Läs guider, tutorials och API-referens. Hitta svar på vanliga frågor i vår knowledge base.</p>
              </div>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">💬 Modal/Dialog</h2>

            <button
              onClick={() => setShowModal(true)}
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg transition-colors"
            >
              Öppna Modal
            </button>

            {showModal && (
              <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                <div className="absolute inset-0 bg-black/50" onClick={() => setShowModal(false)}></div>
                <div 
                  ref={modalRef}
                  className="relative bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 animate-in fade-in zoom-in duration-200"
                >
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-xl font-bold text-gray-900">Bekräfta åtgärd</h3>
                    <button
                      onClick={() => setShowModal(false)}
                      className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                  <p className="text-gray-600 mb-6">
                    Är du säker på att du vill fortsätta? Denna åtgärd kan inte ångras.
                  </p>
                  <div className="flex gap-3">
                    <button
                      onClick={() => setShowModal(false)}
                      className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-2.5 px-4 rounded-lg transition-colors"
                    >
                      Avbryt
                    </button>
                    <button
                      onClick={() => setShowModal(false)}
                      className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2.5 px-4 rounded-lg transition-colors"
                    >
                      Bekräfta
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>

          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">⏳ Skeleton Loaders</h2>

            <button
              onClick={() => {
                setShowSkeleton(true);
                setTimeout(() => setShowSkeleton(false), 3000);
              }}
              className="mb-6 bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors text-sm"
            >
              Ladda igen (3s)
            </button>

            <div className="space-y-4">
              {showSkeleton ? (
                <>
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-gray-200 rounded-full animate-pulse"></div>
                    <div className="flex-1 space-y-2">
                      <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4"></div>
                      <div className="h-3 bg-gray-200 rounded animate-pulse w-1/2"></div>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-gray-200 rounded-full animate-pulse"></div>
                    <div className="flex-1 space-y-2">
                      <div className="h-4 bg-gray-200 rounded animate-pulse w-2/3"></div>
                      <div className="h-3 bg-gray-200 rounded animate-pulse w-1/3"></div>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-gray-200 rounded-full animate-pulse"></div>
                    <div className="flex-1 space-y-2">
                      <div className="h-4 bg-gray-200 rounded animate-pulse w-5/6"></div>
                      <div className="h-3 bg-gray-200 rounded animate-pulse w-2/5"></div>
                    </div>
                  </div>
                </>
              ) : (
                <>
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-bold">A</div>
                    <div className="flex-1">
                      <p className="font-semibold text-gray-900">Anna Andersson</p>
                      <p className="text-sm text-gray-500">Admin</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center text-green-600 font-bold">J</div>
                    <div className="flex-1">
                      <p className="font-semibold text-gray-900">Johan Johansson</p>
                      <p className="text-sm text-gray-500">Användare</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center text-purple-600 font-bold">M</div>
                    <div className="flex-1">
                      <p className="font-semibold text-gray-900">Maria Karlsson</p>
                      <p className="text-sm text-gray-500">Användare</p>
                    </div>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">📅 Date Picker</h2>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Välj datum
                </label>
                <input
                  type="date"
                  value={selectedDate}
                  onChange={(e) => setSelectedDate(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>

              {selectedDate && (
                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <p className="text-sm text-blue-900 font-semibold mb-1">
                    Valt datum:
                  </p>
                  <p className="text-lg text-blue-700 font-bold">
                    {new Date(selectedDate).toLocaleDateString('sv-SE', { 
                      weekday: 'long', 
                      year: 'numeric', 
                      month: 'long', 
                      day: 'numeric' 
                    })}
                  </p>
                </div>
              )}

              <div className="grid grid-cols-7 gap-2 mt-6">
                {['Mån', 'Tis', 'Ons', 'Tor', 'Fre', 'Lör', 'Sön'].map((day) => (
                  <div key={day} className="text-center text-xs font-semibold text-gray-500 py-2">
                    {day}
                  </div>
                ))}
                {Array.from({ length: 31 }, (_, i) => {
                  const date = new Date();
                  const dayOfMonth = i + 1;
                  const isSelected = selectedDate && new Date(selectedDate).getDate() === dayOfMonth;
                  const isToday = date.getDate() === dayOfMonth;
                  
                  return (
                    <button
                      key={i}
                      onClick={() => {
                        const newDate = new Date(date.getFullYear(), date.getMonth(), dayOfMonth);
                        setSelectedDate(newDate.toISOString().split('T')[0]);
                      }}
                      className={`text-center py-2 text-sm rounded-lg transition-colors ${
                        isSelected 
                          ? 'bg-blue-600 text-white font-semibold' 
                          : isToday
                          ? 'bg-blue-100 text-blue-700 font-semibold hover:bg-blue-200'
                          : 'text-gray-700 hover:bg-gray-100'
                      }`}
                    >
                      {dayOfMonth}
                    </button>
                  );
                })}
              </div>
            </div>
          </div>

          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">🔍 Search Bar med Autofyll</h2>

            <div className="relative" ref={searchRef}>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </div>
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => {
                    setSearchQuery(e.target.value);
                    setShowSuggestions(e.target.value.length > 0);
                  }}
                  onFocus={() => setShowSuggestions(searchQuery.length > 0)}
                  placeholder="Sök efter framework..."
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                />
              </div>

              {showSuggestions && filteredSuggestions.length > 0 && (
                <div className="absolute z-10 w-full mt-2 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
                  {filteredSuggestions.map((suggestion) => (
                    <button
                      key={suggestion.id}
                      onClick={() => {
                        setSearchQuery(suggestion.text);
                        setShowSuggestions(false);
                      }}
                      className="w-full px-4 py-3 text-left hover:bg-gray-50 transition-colors flex items-center gap-3"
                    >
                      <span className="text-2xl">{suggestion.emoji}</span>
                      <span className="font-medium text-gray-900">{suggestion.text}</span>
                    </button>
                  ))}
                </div>
              )}

              {searchQuery && (
                <div className="mt-4 p-4 bg-gray-50 rounded-lg">
                  <p className="text-sm text-gray-600">
                    Söker efter: <span className="font-semibold text-gray-900">"{searchQuery}"</span>
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    Hittade {filteredSuggestions.length} resultat
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
