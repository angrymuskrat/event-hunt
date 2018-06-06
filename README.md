# event-hunt
ip of server: 193.124.205.46
port of server: 8000
реализованы следуюшие сервлеты:

	  1) /getEvents 
	  
	  
	  принимает json через post запрос 
	  { 
		    “token” : “токен пользователя”, 
		    “category” : “тип мероприятия”, 
		    “subcategory” : “подтип мероприятия”,  
		    “cost” : “стоимость   билета”, 
		    “eventDate” : “время начала события (unix) по гринвичу”, \
		    “longitude” : “долгота точки вокруг которой ищутся события”, 
		    “latitude” : “широта”, 
		    “userDate” : “системное время (unix) по гринвичу” 
	   }

	   в качестве ответа отправляет json
	   { 
		    “data” : [{ 
				“name” : “название события”, 
				”eventDate” : “unix дата события по Гринвичу”, 
				“cost” : “стоимость события”,  
				“description” : “описание события”, 
				“photo” : "фото в base64"
				“ticketPlaces” : [{ 
					  “type” : “URL (ссылка) или физический адрес или free”, 
					  “content” : “ссылка или адрес или надпись ‘вход свободный’” 
				}],  
				“longitude” : “долгота мероприятия”, 
				“latitude” : “широта мероприятия”, 
				“links” : {
					  “vk” : “ссылка на группу в вк”, 
					  “facebook” : “ссылка на группу в facebook”, 
					  “instagram” : “ссылка на канал в инсте”
				  }
		      }] 
	    } <br>
    
    
	2)/addEvent
  	принимает post запрос типа json 
	{
		"token": "sqd15udxgjujiq64yk5d7w1q1fxdg7ta5k6mtejl",
		"name" : "DEEP PURPLE - ЮБИЛЕЙНЫЙ КОНЦЕРТ 50 ЛЕТ", 
		"category": "Concert",
		"subcategory": "Metal",
		"eventDate" : "1527868800", 
		"cost" : "3000",  
		"photo" : "Fgkidfgjdlgjodgod", 
		"ticketPlaces" : [
			{ 
				"type" : "URL", 
				"content" : "https://spb.kassir.ru/koncert/ledovyiy-dvorets/deep-purple---yubileynyiy-kontsert-50-let_2018-06-01_1900" 
			}, { 
				"type" : "place", 
				"content" : "ЛЕДОВЫЙ ДВОРЕЦ , г. Санкт-Петербург, пр. Пятилеток, 1" 
			}
		],  
		"longitude" : "30.468154", 
		"latitude" : "59.921512", 
		"links" : {
			"vk" : "https://vk.com/deep_purple_spb", 
			"facebook" : "", 
			"instagram" : ""
		}
	}
	ответ следующего вида
	{"status":"SUCCESS","errorCode":null}<br>
	3) /signIn
	  post запрос след вида
	  {
	      "googleToken": ""
	  }
	  ответ
	  {
	  	"token":"pxsl0ufynjt2oqf039xkkr7x9ogwxv8g442eicdc",
		"isFirst":true
	  }


