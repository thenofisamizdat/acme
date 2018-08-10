(function() {
    'use strict';
    angular
        .module('acmeApp')
        .factory('QuestionType', QuestionType);

    QuestionType.$inject = ['$resource'];

    function QuestionType ($resource) {
        var resourceUrl =  'api/question-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
